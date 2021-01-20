package org.chen.timer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.chen.constant.BusinessConstant;
import org.chen.domain.dto.weixin.GetAccessTokenDto;
import org.chen.domain.entity.DataDict;
import org.chen.domain.result.StatisticsItemResult;
import org.chen.property.RedisPrefixProperties;
import org.chen.rpc.responsehandler.WeixinAccessRespHandler;
import org.chen.service.IDataDictService;
import org.chen.util.BusinessUtil;
import org.chen.util.http.OkHttpResponseUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务类
 *
 * @author YuChen
 * @date 2019/6/13
 **/

@Slf4j
@Service
public class UpdateTimer {

    private String weixinTokenPrefix;

    private String weixinTokenLock;

    private WeixinAccessToken weixinAccessToken;

    private RedissonClient redissonClient;

    private static String templateId = "D41231";

    private static List<StatisticsItemResult> statisticsItemList;

    private IDataDictService dataDictService;


    @Autowired
    private UpdateTimer(RedisPrefixProperties redisPrefixProperties, RedissonClient redissonClient,IDataDictService dataDictService){
        this.redissonClient = redissonClient;
        this.weixinTokenLock = redisPrefixProperties.getWeixinTokenLock();
        this.weixinTokenPrefix = redisPrefixProperties.getWeixinTokenPrefix();
        this.dataDictService = dataDictService;
        updateDataDict();
    }


    @Scheduled(cron = "0 0 */1 * * ?")
    public void updateTokenJob(){
        log.info("开始更新token");
        updateToken();
    }

    @Scheduled(cron = "0 0 */8 * * ?")
    public void updateDataDict(){
        // 如果经常改  可以用redis做监听, 配合后台系统实时修改
        log.info("开始更新dict");
        DataDict templateIdDict = dataDictService.getOne(new QueryWrapper<DataDict>().eq("dict_code", "template_id"));
        if(templateIdDict == null){
            log.warn("templateIdDict为空!");
        }else {
            UpdateTimer.templateId = templateIdDict.getDictValue();
        }
        DataDict statisticsItemDict = dataDictService.getOne(new QueryWrapper<DataDict>().eq("dict_code", "statistics_list"));
        if(statisticsItemDict == null){
            log.warn("statisticsItemDict为空!");
        }else {
            String dictValue = statisticsItemDict.getDictValue();
            List<StatisticsItemResult> statisticsItemResults = JSONObject.parseArray(dictValue, StatisticsItemResult.class);
            if(CollectionUtil.isNotEmpty(statisticsItemResults)){
                UpdateTimer.statisticsItemList = statisticsItemResults;
            }
        }

    }

    public static String getTemplateId() {
        return UpdateTimer.templateId;
    }

    public static List<StatisticsItemResult>  getStatisticsItemList() {
        return UpdateTimer.statisticsItemList;
    }

    public String getToken(){
        if(this.weixinAccessToken == null){
            this.weixinAccessToken = getTokenFromRedis();
        }
        if(this.weixinAccessToken == null){
            updateToken();
            if(this.weixinAccessToken == null){
                log.error("初始化sessionId失败");
                return null;
            }
        }
        return this.weixinAccessToken.getToken();
    }

    private WeixinAccessToken getTokenFromRedis(){
        //RBucket<SessionId> bucket = redissonClient.getBucket(RedissConstant.SESSION_ID);
        RBucket<WeixinAccessToken> bucket = redissonClient.getBucket(weixinTokenPrefix);
        if(!bucket.isExists()){
            return null;
        }
        return bucket.get();
    }

    public void updateToken() {
        // 目前单机  可以用juc的锁替代  但是懒得改了
        RLock lock = null;
        boolean lockSuccess = false;
        try{
            lock = redissonClient.getLock(weixinTokenLock);
            lockSuccess = lock.tryLock();
            if(lockSuccess){
                log.info("更新weixintoken获取锁成功");
                RBucket<WeixinAccessToken> bucket = redissonClient.getBucket(weixinTokenPrefix);
                if(bucket.isExists()){
                    WeixinAccessToken redisToken = bucket.get();
                    log.info("验证redis中的weixintoken是否是刚更新的");
                    Date updateDate = redisToken.getUpdateDate();
                    if(updateDate != null){
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.MINUTE,-5);
                        Calendar updateDateCalendar = Calendar.getInstance();
                        updateDateCalendar.setTime(updateDate);
                        boolean before = now.before(updateDateCalendar);
                        if(before){
                            log.info("从redis中获取到的weixintoken为5分钟内更新, 可以直接使用");
                            this.weixinAccessToken = redisToken;
                            return;
                        }
                    }
                }

                String newWeixinToken = getWeixinToken();
                if(StrUtil.isBlank(newWeixinToken)){
                    log.error("未获取到新的weixintoken");
                    throw new RuntimeException("系统错误,请联系管理员");
                }
                // RBucket<String> bucket = redissonClient.getBucket(RedissConstant.SESSION_ID);
                WeixinAccessToken newWeixinTokenEntity = new WeixinAccessToken(newWeixinToken,new Date());
                bucket.set(newWeixinTokenEntity,2,TimeUnit.HOURS);
                log.info("旧的token:{},新的token:{}",this.weixinAccessToken,newWeixinTokenEntity);
                this.weixinAccessToken = newWeixinTokenEntity;
            }else {
                // 等待5s后从redis获取
                log.info("等待5s后从redis获取");
                Thread.sleep(5000);
                RBucket<WeixinAccessToken> bucket = redissonClient.getBucket(weixinTokenPrefix);
                WeixinAccessToken newWeixinTokenEntity = bucket.get();
                if(!bucket.isExists()){
                    log.warn("无法从数据库中获取到toeknEntity!");
                    return;
                }
                log.info("旧的token:{},新的token:{}",this.weixinAccessToken,newWeixinTokenEntity);
                this.weixinAccessToken = newWeixinTokenEntity;
            }
        } catch (InterruptedException e) {

        } finally {
            if(lock != null && lockSuccess){
                lock.unlock();
            }
        }
    }

    private String getWeixinToken() {
        Request request = new Request.Builder().url(BusinessConstant.GET_ACCESSTOKEN_WITH_PARAM).build();
        OkHttpResponseUtils.JsonResult<GetAccessTokenDto, Object> result = OkHttpResponseUtils.getResultFromJsonBody(request, GetAccessTokenDto.class,
                null, false, true, "获取微信accessToken");
        OkHttpResponseUtils.baseErrorHandle(result, new WeixinAccessRespHandler(), BusinessConstant.GET_ACCESSTOKEN_WITH_PARAM, "获取微信accessToken");
        GetAccessTokenDto token = result.getJsonRes();
        return token.getAccess_token();
    }


    public UpdateTimer(){}

    public static void main(String[] args) {
        UpdateTimer updateWeixinTokenTimer = new UpdateTimer();
        String weixinToken = updateWeixinTokenTimer.getWeixinToken();
        System.out.println(weixinToken);
        BusinessUtil.sendCronJobMsg(weixinToken,"oF8ZQ5SZhmcv-q4CDs9DyNRonq6I",1,1,10L);
    }



    public static class WeixinAccessToken{
        String token;

        Date updateDate;

        public WeixinAccessToken(){}

        public WeixinAccessToken(String token, Date updateDate) {
            this.token = token;
            this.updateDate = updateDate;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Date getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
        }

        @Override
        public String toString() {
            return "WeixinAccessToken{" +
                    "token='" + token + '\'' +
                    ", updateDate=" + updateDate +
                    '}';
        }
    }

}

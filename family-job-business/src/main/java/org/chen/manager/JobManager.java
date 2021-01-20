package org.chen.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.chen.constant.BusinessConstant;
import org.chen.domain.dto.CheckStepDto;
import org.chen.domain.dto.JobUserCountDto;
import org.chen.domain.dto.weixin.WeixinSessionDto;
import org.chen.domain.dto.weixin.WxRunDataDto;
import org.chen.domain.entity.*;
import org.chen.domain.param.*;
import org.chen.domain.result.*;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.service.*;
import org.chen.util.BusinessUtil;
import org.chen.util.DateUtils;
import org.chen.util.WeChatMiniProgramUtil;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 券信息 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
@Slf4j
@Service
public class JobManager {
    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IJobInfoService jobInfoService;

    @Autowired
    private IJobUserService jobUserService;

    @Autowired
    private IJobTypeService jobTypeService;

    @Autowired
    private ICronJobService cronJobService;

    @Autowired
    private IPointRecordService pointRecordService;

    @Autowired
    private IStepRecordService stepRecordService;

    /**
     * 统计各类Job的数量
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/11/30 10:47
     */
    public JobCountResult jobCount() {
        UserInfo userInfo = userInfoService.getNowUserInfo();
        Long userId = userInfo.getId();
        List<JobUserCountDto> count = jobUserService.jobCount(userId);
        if (CollectionUtil.isEmpty(count)) {
            return null;
        }
        Long myToCheck = 0L;
        Long myToFinish = 0L;
        Long myFinished = 0L;
        Long myExpired = 0L;
        Long myRefused = 0L;

        Long othersToCheck = 0L;
        Long othersToFinish = 0L;
        Long othersFinished = 0L;
        Long othersExpired = 0L;
        Long othersRefused = 0L;
        for (JobUserCountDto dto : count) {
            Long acceptId = dto.getUserId();
            Long creatorId = dto.getCreatorId();
            boolean iCreator = creatorId.equals(userId);
            boolean iAccept = acceptId.equals(userId);
            Long thisCount = dto.getCount();
            switch (dto.getState()) {
                case BusinessConstant.JOB_USER_STATE.TO_CHECK:
                    if (iAccept) {
                        myToCheck += thisCount;
                    }
                    if (iCreator) {
                        othersToCheck += thisCount;
                    }
                    break;
                case BusinessConstant.JOB_USER_STATE.EXPIRED:
                    if (iAccept) {
                        myExpired += thisCount;
                    }
                    if (iCreator) {
                        othersExpired += thisCount;
                    }
                    break;
                case BusinessConstant.JOB_USER_STATE.REFUSED:
                    if (iAccept) {
                        myRefused += thisCount;
                    }
                    if (iCreator) {
                        othersRefused += thisCount;
                    }
                    break;
                case BusinessConstant.JOB_USER_STATE.TO_DO:
                    if (iAccept) {
                        myToFinish += thisCount;
                    }
                    if (iCreator) {
                        othersToFinish += thisCount;
                    }
                    break;
                case BusinessConstant.JOB_USER_STATE.FINISHED:
                    if (iAccept) {
                        myFinished += thisCount;
                    }
                    if (iCreator) {
                        othersFinished += thisCount;
                    }
                    break;
                default:
                    log.error("查询到非法状态");
                    throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
            }
        }

        JobCountResult res = new JobCountResult();
        res.setMyToCheck(myToCheck);
        res.setMyToFinish(myToFinish);
        res.setMyExpired(myExpired);
        res.setMyRefused(myRefused);
        res.setMyFinished(myFinished);

        res.setOthersRefused(othersRefused);
        res.setOthersExpired(othersExpired);
        res.setOthersToFinish(othersToFinish);
        res.setOthersToCheck(othersToCheck);
        res.setOthersFinished(othersFinished);
        return res;
    }

    /**
     * 任务详情
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:20
     */
    public JobUserResult getJobUserDetail(Long jobUserId) {
        UserInfo user = userInfoService.getNowUserInfo();
        JobUser jobUser = jobUserService.getById(jobUserId);
        if (jobUser == null) {
            throw new BusinessException("找不到任务", 404);
        }
        Long userId = user.getId();
        if (!userId.equals(jobUser.getCreatorId()) && !userId.equals(jobUser.getUserId())) {
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        JobUserResult res = new JobUserResult();
        BeanUtil.copyProperties(jobUser, res);
        res.setStateStr(getStateStr(res.getState()));
        if (res.getCronType() != null) {
            res.setCronTypeStr(BusinessConstant.CRON_TYPE.MAP.get(res.getCronType()));
        } else {
            res.setCronTypeStr("");
        }
        res.setCreateTypeStr(BusinessConstant.CREATE_TYPE.MAP.get(res.getCreateType()));
        String allTypeStr = res.getCreateTypeStr() + res.getCronTypeStr();
        res.setAllTypeStr(allTypeStr);
        return res;
    }

    /**
     * 发布任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/11/24 10:24
     */
    @Transactional(rollbackFor = Exception.class)
    public UserInfo createJobToUser(CreateJobToUserParam param) {
        Integer canMoney = param.getCanMoney();
        BigDecimal cost = param.getCost();
        if (canMoney != null && canMoney == 1) {
            if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("可支付家务需填写正确的金额", 402);
            }
        }
        Integer canStep = param.getCanStep();
        Long costStep = param.getCostStep();
        if (canStep != null && canStep == 1) {
            if (costStep == null || costStep <= 0) {
                throw new BusinessException("可步数抵扣的家务需填写正确的步数", 402);
            }
        }
        UserInfo creator = userInfoService.getNowUserInfo();
        UserInfo user = userInfoService.getById(param.getUserId());
        if (user == null) {
            log.warn("找不到接受者的信息，id:{}", param.getUserId());
            throw new BusinessException("找不到用户信息", 404);
        }

        JobInfo job = jobInfoService.getById(param.getJobId());
        if (job == null || job.getDeleted() != 0) {
            throw new BusinessException("该家务不存在或已删除", 404);
        }
//        if (!job.getFamilyId().equals(creator.getFamilyId())) {
//            throw new BusinessException("该家务不属于该家庭", 404);
//        }
        if (!creator.getFamilyId().equals(user.getFamilyId())) {
            throw new BusinessException("该成员不属于本家庭", 405);
        }
        if (creator.getId().equals(user.getId())) {
            throw new BusinessException("不能给自己发布家务", 405);
        }
        Long points = param.getPoints();
        if (creator.getPoints() < points) {
            throw new BusinessException("您的家务点不足", 405);
        }

        JobUser jobUser = buildJobUser(job, creator, user, param.getExpireTime()
                , param.getDesc(), param.getCost(), param.getCanMoney(), param.getCanStep()
                , param.getCostStep(), points);
        creator.setPoints(creator.getPoints() - points);
        creator.setUpdateTime(new Date());
        jobUserService.save(jobUser);
        // 扣除家务点
        int row = userInfoService.updateRoleWithVersionLock(creator);
        if (row == 0) {
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        pointRecordService.createPointRecord(jobUser.getId(), jobUser.getFamilyId(), creator.getId()
                , points, BusinessConstant.POINT_RECORD_TYPE.ZHICHU_ZHIPAI);
        //todo 发送通知给接收人

        return creator;
    }

    /**
     * 接受任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/4 14:23
     */
    @Transactional(rollbackFor = Exception.class)
    public JobUserResult acceptJob(Long jobUserId) {
        JobUser jobUser = jobUserService.getById(jobUserId);
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        changeState(nowUserInfo,jobUser, BusinessConstant.JOB_USER_STATE.TO_CHECK
                , BusinessConstant.JOB_USER_STATE.TO_DO, true);
        Date now = new Date();
        jobUser.setUpdateTime(now);
        jobUser.setCheckTime(now);
        jobUserService.updateById(jobUser);
        JobUserResult res = new JobUserResult();
        BeanUtil.copyProperties(jobUser, res);
        res.setStateStr(getStateStr(res.getState()));
        // todo 通知

        return res;
    }

    /**
     * 拒绝任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:20
     */
    @Transactional(rollbackFor = Exception.class)
    public JobUserResult refuseJob(Long jobUserId, String refuseReason) {
        JobUser jobUser = jobUserService.getById(jobUserId);
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        changeState(nowUserInfo,jobUser, BusinessConstant.JOB_USER_STATE.TO_CHECK
                , BusinessConstant.JOB_USER_STATE.REFUSED, true);
        Date now = new Date();
        jobUser.setUpdateTime(now);
        jobUser.setCheckTime(now);
        jobUser.setRefuseReason(refuseReason);
        jobUserService.updateById(jobUser);
        JobUserResult res = new JobUserResult();
        BeanUtil.copyProperties(jobUser, res);
        res.setStateStr(getStateStr(res.getState()));

        // 恢复点数
        UserInfo userInfo = userInfoService.getById(jobUser.getCreatorId());
        userInfo.setUpdateTime(new Date());
        userInfo.setPoints(userInfo.getPoints() + jobUser.getPoints());
        int row = userInfoService.updateRoleWithVersionLock(userInfo);
        if (row == 0) {
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        pointRecordService.createPointRecord(jobUser.getId(), jobUser.getFamilyId(), userInfo.getId()
                , jobUser.getPoints(), BusinessConstant.POINT_RECORD_TYPE.SHOURU_DINGSHIHUIFU);
        // todo 通知

        return res;
    }


    /**
     * 完成任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:20
     */
    @Transactional(rollbackFor = Exception.class)
    public JobUserResult finishJob(Long jobUserId) {
        //  创建者和执行者可同时操作 要加锁
        JobUser jobUser = jobUserService.getInOtherTransaction(jobUserId);
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        changeState(nowUserInfo,jobUser, BusinessConstant.JOB_USER_STATE.TO_DO
                , BusinessConstant.JOB_USER_STATE.FINISHED, false);
        Date now = new Date();
        jobUser.setUpdateTime(now);
        jobUser.setFinishTime(now);
        jobUser.setStepFinish(0);
        int affectedRows = jobUserService.updateStateWithVersion(jobUser);
        if (affectedRows == 0) {
            log.info("出现并发操作,jobUser:{}", jobUser);
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        // 增加点数
        UserInfo userInfo = userInfoService.getById(jobUser.getUserId());
        userInfo.setUpdateTime(new Date());
        userInfo.setPoints(userInfo.getPoints() + jobUser.getPoints());
        int row = userInfoService.updateRoleWithVersionLock(userInfo);
        if (row == 0) {
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        Integer createType = jobUser.getCreateType();
        Integer recordType;
        switch (createType){
            case BusinessConstant.CREATE_TYPE.USER:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_ZHIPAI;
                break;
            case BusinessConstant.CREATE_TYPE.CRON:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_DINGSHI;
                break;
            case BusinessConstant.CREATE_TYPE.EXTRA:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_EWAI;
                break;
            default:
                log.error("错误的任务创建类型:jobUser{}",jobUser);
                throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }

        pointRecordService.createPointRecord(jobUser.getId(), jobUser.getFamilyId(), userInfo.getId()
                , jobUser.getPoints(), recordType);
        JobUserResult res = new JobUserResult();
        BeanUtil.copyProperties(jobUser, res);
        res.setStateStr(getStateStr(res.getState()));
        // todo 通知

        return res;
    }


    /**
     * 家庭cronjob列表
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 9:58
     */
    public IPage<CronJobResult>listFamilyCronJob(Integer pageNum, Integer pageSize) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        IPage<CronJob> page = cronJobService.page(new Page<>(pageNum + 1, pageSize),new QueryWrapper<CronJob>()
                .eq("family_id", nowUserInfo.getFamilyId())
                .ne("state", BusinessConstant.CRON_STATE.DELETED)
                .orderByDesc("update_time"));
        List<CronJob> list = page.getRecords();
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        IPage<CronJobResult> resPage = new Page<>();
        resPage.setCurrent(page.getCurrent());
        resPage.setPages(page.getPages());
        resPage.setSize(page.getSize());
        resPage.setTotal(page.getTotal());
        List<CronJobResult> res = new ArrayList<>();
        for (CronJob cronJob : list) {
            CronJobResult cronJobResult = new CronJobResult();
            BeanUtil.copyProperties(cronJob, cronJobResult);
            cronJobResult.setCronTypeStr(BusinessConstant.CRON_TYPE.MAP.get(cronJobResult.getType()));
            cronJobResult.setStateStr(BusinessConstant.CRON_STATE.MAP.get(cronJobResult.getState()));
            res.add(cronJobResult);
        }
        resPage.setRecords(res);
        return resPage;
    }


    /**
     * 新建定时任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:20
     */
    @Transactional(rollbackFor = Exception.class)
    public CronJobResult createCronJob(CreateCronJobToUserParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        if (!nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.OWNER)) {
            throw new BusinessException("只有家长能添加定时任务", 407);
        }
        UserInfo user = userInfoService.getById(param.getUserId());
        if (user == null) {
            log.warn("找不到接受者的信息，id:{}", param.getUserId());
            throw new BusinessException("找不到用户信息", 404);
        }
        if (!nowUserInfo.getFamilyId().equals(user.getFamilyId())) {
            throw new BusinessException("该成员不属于本家庭", 405);
        }
        if (user.getWatchdogId() == null) {
            throw new BusinessException("该成员未设置监督人", 404);
        }
        JobInfo job = jobInfoService.getById(param.getJobId());
        if (job == null || job.getDeleted() != 0) {
            throw new BusinessException("该家务不存在或已删除", 404);
        }
        CronJob cronJob = buildCronJob(param, nowUserInfo, user, job);
        cronJobService.save(cronJob);
        CronJobResult res = new CronJobResult();
        BeanUtil.copyProperties(cronJob, res);
        if(param.getEnableNow() != null && param.getEnableNow() == 1){
            // 用户选择了立即生效
            createByCronJob(cronJob,job,true);
        }
        return res;
    }

    /**
     * 定时任务详情
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:46
     */
    public CronJobResult cronJobDetail(Long cronJobId) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        CronJob cronJob = cronJobService.getById(cronJobId);
        if (cronJob == null) {
            throw new BusinessException("找不到定时任务", 404);
        }
        if (!nowUserInfo.getFamilyId().equals(cronJob.getFamilyId())) {
            throw new BusinessException("该任务不属于您的家庭", 405);
        }
        CronJobResult res = new CronJobResult();
        BeanUtil.copyProperties(cronJob, res);
        res.setCronTypeStr(BusinessConstant.CRON_TYPE.MAP.get(res.getType()));
        res.setStateStr(BusinessConstant.CRON_STATE.MAP.get(res.getState()));
        return res;
    }

    /**
     * 关闭定时任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 10:52
     */
    @Transactional(rollbackFor = Exception.class)
    public CronJobResult closeCronJob(CronJobDetailParam param) {
        CronJob cronJob = changeCronJobState(param, BusinessConstant.CRON_STATE.UNABLE);
        CronJobResult res = new CronJobResult();
        BeanUtil.copyProperties(cronJob, res);
        return res;
    }

    private CronJob changeCronJobState(CronJobDetailParam param, Integer toState) {
        CronJob cronJob = cronJobService.getById(param.getCronJobId());
        if (cronJob == null) {
            throw new BusinessException("找不到定时任务", 404);
        }
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (!nowUserInfo.getFamilyId().equals(cronJob.getFamilyId())) {
            throw new BusinessException("该任务不属于您的家庭", 405);
        }
        if (!nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.OWNER)) {
            throw new BusinessException("只有家长能改变定时任务状态", 405);
        }
        cronJob.setUpdateTime(new Date());
        cronJob.setState(toState);
        int row = cronJobService.updateCronJobWithLock(cronJob);
        if (row == 0) {
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        return cronJob;
    }

    /**
     * 开启定时任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 11:00
     */
    @Transactional(rollbackFor = Exception.class)
    public CronJobResult openCronJob(CronJobDetailParam param) {
        CronJob cronJob = changeCronJobState(param, BusinessConstant.CRON_STATE.ENABLE);
        CronJobResult res = new CronJobResult();
        BeanUtil.copyProperties(cronJob, res);
        return res;
    }

    /**
     * 删除定时任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 11:00
     */
    @Transactional(rollbackFor = Exception.class)
    public CronJobResult deleteCronJob(CronJobDetailParam param) {
        CronJob cronJob = changeCronJobState(param, BusinessConstant.CRON_STATE.DELETED);
        CronJobResult res = new CronJobResult();
        BeanUtil.copyProperties(cronJob, res);
        return res;
    }


    /**
     * 使用步数完成
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 13:39
     */
    @Transactional(rollbackFor = Exception.class)
    public JobUserResult finishJobByStep(FinishByStepParam param) {
        JobUser jobUser = jobUserService.getById(param.getJobUserId());
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        changeState(nowUserInfo,jobUser, BusinessConstant.JOB_USER_STATE.TO_DO
                , BusinessConstant.JOB_USER_STATE.FINISHED, true);
        if(!jobUser.getCanStep().equals(1)){
            throw new BusinessException("该任务未设置可用步数抵扣",422);
        }
        CheckStepDto canStepFinish = checkAndUpdateStep(jobUser,param.getCode()
                ,param.getEncryptedData(),param.getIv(),nowUserInfo.getId());

        if(!canStepFinish.getCanFinish()){
            Long remainingStep = canStepFinish.getRecord().getRemainingStep();
            String msg = "您剩余的可用步数为"+remainingStep+"步,不足以抵扣该任务";
            throw new BusinessException(msg,422);
        }

        Date now = new Date();
        jobUser.setUpdateTime(now);
        jobUser.setFinishTime(now);
        jobUser.setStepFinish(1);
        jobUser.setStepId(canStepFinish.getRecord().getId());
        int affectedRows = jobUserService.updateStateWithVersion(jobUser);
        if (affectedRows == 0) {
            log.info("出现并发操作,jobUser:{}", jobUser);
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        // 增加点数
        nowUserInfo.setUpdateTime(new Date());
        nowUserInfo.setPoints(nowUserInfo.getPoints() + jobUser.getPoints());
        int row = userInfoService.updateRoleWithVersionLock(nowUserInfo);
        if (row == 0) {
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        Integer createType = jobUser.getCreateType();
        Integer recordType;
        switch (createType){
            case BusinessConstant.CREATE_TYPE.USER:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_ZHIPAI;
                break;
            case BusinessConstant.CREATE_TYPE.CRON:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_DINGSHI;
                break;
            case BusinessConstant.CREATE_TYPE.EXTRA:
                recordType = BusinessConstant.POINT_RECORD_TYPE.SHOURU_EWAI;
                break;
            default:
                log.error("错误的任务创建类型:jobUser{}",jobUser);
                throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }

        pointRecordService.createPointRecord(jobUser.getId(), jobUser.getFamilyId(), nowUserInfo.getId()
                , jobUser.getPoints(), recordType);
        JobUserResult res = new JobUserResult();
        BeanUtil.copyProperties(jobUser, res);
        res.setStateStr(getStateStr(res.getState()));
        // todo 通知

        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public CheckStepDto checkAndUpdateStep(JobUser jobUser, String code, String encryptedData
            ,String iv, Long userId) {
        StepRecord yesterdayRecord = stepRecordService.getYesterdayRecord(userId);
        if(yesterdayRecord == null){
            // 第一次获取  解密数据并保存
            yesterdayRecord = initStepRecord(code,encryptedData,iv,userId);
            // 在新的事物中保存  防止回滚
            stepRecordService.saveInOtherTransaction(yesterdayRecord);
        }
        CheckStepDto res = new CheckStepDto();
        res.setCanFinish(false);
        Long remainingStep = yesterdayRecord.getRemainingStep();
        remainingStep = remainingStep - jobUser.getCostStep();
        if(remainingStep > 0){
            yesterdayRecord.setRemainingStep(remainingStep);
            res.setCanFinish(true);
            int row = stepRecordService.updateRemainingStepWithLock(yesterdayRecord);
            if(row == 0){
                throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
            }
        }else {
            res.setCanFinish(false);
        }
        res.setRecord(yesterdayRecord);
        return res;
    }

    private StepRecord initStepRecord(String code, String encryptedData,String iv, Long userId) {
        WeixinSessionDto weixinSessionDto = WeChatMiniProgramUtil.jsCode2Session(
                BusinessConstant.APP_ID, BusinessConstant.APP_SECRET, code);
        if(weixinSessionDto == null || StrUtil.isEmpty(weixinSessionDto.getSession_key())){
            throw new BusinessException("解密步数数据失败",422);
        }
        WxRunDataDto runDataDto = WeChatMiniProgramUtil.decryptUserInfo(encryptedData
                , weixinSessionDto.getSession_key(), iv,WxRunDataDto.class);
        if(runDataDto == null){
            throw new BusinessException("解密步数数据失败",422);
        }
        WxRunDataDto.StepInfo yesterdayStepInfo = getYesterdayStepInfo(runDataDto);
        if(yesterdayStepInfo == null){
            throw new BusinessException("无法获取到昨日步数",422);
        }
        StepRecord res = new StepRecord();
        res.setAllStep(yesterdayStepInfo.getStep());
        // 可能出现问题  但是不会有人无聊到0点来试试吧
        res.setStartDate(DateUtils.getYesterdayDateStr());
        res.setRemainingStep(yesterdayStepInfo.getStep());
        Date now = new Date();
        res.setCreateTime(now);
        res.setUpdateTime(now);
        res.setUserId(userId);
        res.setTimestamp(yesterdayStepInfo.getTimestamp());
        res.setVersion(0L);
        return res;
    }

    private WxRunDataDto.StepInfo getYesterdayStepInfo(WxRunDataDto runDataDto) {
        if(runDataDto == null || CollectionUtil.isEmpty(runDataDto.getStepInfoList())){
            return null;
        }
        List<WxRunDataDto.StepInfo> stepInfoList = runDataDto.getStepInfoList();
        if(stepInfoList.size() < 2){
            return null;
        }
        // 倒数第二个是昨天的
        return stepInfoList.get(stepInfoList.size() - 2);
    }


    private CronJob buildCronJob(CreateCronJobToUserParam param, UserInfo owner, UserInfo user, JobInfo job) {
        CronJob cronJob = new CronJob();
        Integer type = param.getType();
        String legal = BusinessConstant.CRON_TYPE.MAP.get(type);
        Integer canStep = param.getCanStep();
        Long costStep = param.getCostStep();
        if (canStep != null && canStep == 1) {
            if (costStep == null || costStep <= 0) {
                throw new BusinessException("可步数抵扣的家务需填写正确的步数", 402);
            }
        }
        if (legal == null) {
            throw new BusinessException("定时类型不合法", 400);
        }
        cronJob.setType(type);
        cronJob.setState(BusinessConstant.CRON_STATE.ENABLE);
        cronJob.setJobId(job.getId());
        cronJob.setName(job.getName());
        cronJob.setTypeId(job.getTypeId());
        cronJob.setFamilyId(user.getFamilyId());
        cronJob.setUserId(user.getId());
        cronJob.setUserAvatar(user.getAvatar());
        cronJob.setUserName(user.getNickName());
        cronJob.setWatchdogId(user.getWatchdogId());
        cronJob.setWatchdogName(user.getWatchdogName());
        cronJob.setWatchdogAvatar(user.getWatchdogAvatar());
        cronJob.setPoints(param.getPoints());
        if (param.getTimes() > 100) {
            throw new BusinessException("次数不能大于100", 412);
        }
        cronJob.setTimes(param.getTimes());
        Date now = new Date();
        cronJob.setCreateTime(now);
        cronJob.setUpdateTime(now);
        cronJob.setRemark(param.getRemark());
        cronJob.setVersion(0L);
        cronJob.setCreatorId(owner.getId());
        cronJob.setCreatorAvatar(owner.getAvatar());
        cronJob.setCreatorName(owner.getNickName());
        cronJob.setCanStep(param.getCanStep());
        cronJob.setCostStep(param.getCostStep());
        return cronJob;
    }

    /**
     * 任务列表
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 15:03
     */
    public IPage<JobUserResult> listMyJobUser(Long userId, Integer state, Integer isMineJob
            , Integer pageNum, Integer pageSize) {
        UserInfo user = userInfoService.getById(userId);
        if (user == null) {
            log.warn("找不到个人信息，id:{}", userId);
            throw new BusinessException("找不到用户信息", 404);
        }
        QueryWrapper<JobUser> queryWrapper = new QueryWrapper<>();
        if (isMineJob == 1) {
            queryWrapper.eq("user_id", userId);
        } else {
            queryWrapper.eq("creator_id", userId);

        }
        if (state != -1) {
            queryWrapper.eq("state", state);
        }
        queryWrapper.orderByDesc("update_time");
        IPage<JobUser> page = jobUserService.page(new Page<>(pageNum + 1, pageSize), queryWrapper);
        List<JobUser> list = page.getRecords();
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        // 查一下是否过期
        list = filterAndUpdateIfExpire(list);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        Page<JobUserResult> res = new Page<>();
        res.setCurrent(page.getCurrent());
        res.setTotal(page.getTotal());
        res.setSize(page.getSize());
        List<JobUserResult> resList = new ArrayList<>();
        for (JobUser jobUser : list) {
            JobUserResult jobUserResult = new JobUserResult();
            BeanUtil.copyProperties(jobUser, jobUserResult);
            jobUserResult.setStateStr(getStateStr(jobUserResult.getState()));
            if (jobUserResult.getCronType() != null) {
                jobUserResult.setCronTypeStr(BusinessConstant.CRON_TYPE.MAP.get(jobUserResult.getCronType()));
            } else {
                jobUserResult.setCronTypeStr("");
            }
            jobUserResult.setCreateTypeStr(BusinessConstant.CREATE_TYPE.MAP.get(jobUserResult.getCreateType()));
            String allTypeStr = jobUserResult.getCreateTypeStr() + jobUserResult.getCronTypeStr();
            jobUserResult.setAllTypeStr(allTypeStr);
            resList.add(jobUserResult);
        }
        res.setRecords(resList);
        return res;
    }

    /**
     * 根据定时任务创建任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/15 13:41
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void createByCronJob(CronJob cronJob, JobInfo jobInfo,boolean enableNow) {
        // 如果用户在00:01:00的时候添加了立即生效任务  会重复  但是无所谓了
        Calendar now = Calendar.getInstance();
        Date expireTime;
        Integer type = cronJob.getType();
        boolean willCreate = false;
        if (type.equals(BusinessConstant.CRON_TYPE.DAY)) {
            // 每日任务每天都加
            willCreate = true;
            now.add(Calendar.DATE,1);
            expireTime = DateUtils.getNextDayStartTime();
        }else if (type.equals(BusinessConstant.CRON_TYPE.WEEK)) {
            // 每周一添加周度任务
            boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            if (isFirstSunday) {
                willCreate = dayOfWeek == 2;
            } else {
                willCreate = dayOfWeek == 1;
            }
            expireTime = DateUtils.getNextWeekDayStartTime();
        }else if (type.equals(BusinessConstant.CRON_TYPE.MONTH)) {
            // 每月第一天添加月度任务
            willCreate = now.get(Calendar.DAY_OF_MONTH) == 1;
            expireTime = DateUtils.getNextMonthStartTime();
        }else {
            log.error("类型错误:cronJob:{}",cronJob);
            throw new BusinessException("类型错误",510);
        }
        if(enableNow){
            willCreate = true;
        }
        if (willCreate) {
            List<JobUser> list = new ArrayList<>();
            Integer times = cronJob.getTimes();
            for (int i = 0; i < times; i++) {
                JobUser jobUser = initJobUserByCronJob(cronJob, jobInfo,expireTime);
                list.add(jobUser);
            }
            jobUserService.saveBatch(list);
        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.getNextWeekDayStartTime());
        System.out.println(DateUtils.getNextMonthStartTime());
        System.out.println(DateUtils.getNextDayStartTime());
    }

    /**
     * 刷新订阅消息权限
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/16 17:37
     */
    @Transactional(rollbackFor = Exception.class)
    public void freshSendMsg(RefreshUserCanSendParam param) {
        Long userIdFromRequest = TokenUtil.getUserIdFromRequest();
        UserInfo userInfo = userInfoService.getUserInOtherTransaction(userIdFromRequest);
        userInfo.setCanSend(param.getCanSend());
        for (int i = 0; i < 3; i++) {
            int row = userInfoService.updateRoleWithVersionLock(userInfo);
            if(row != 0){
                break;
            }
            if(i == 2){
                log.warn("刷新订阅权限重复三次依旧锁定,userInfo:{}",userInfo);
            }
        }
    }

    /**
     * 领取额外任务
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/22 11:02
     */
    @Transactional(rollbackFor = Exception.class)
    public JobUser receiveJobUser(ReceiveJobUserParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        if (nowUserInfo.getWatchdogId() == null) {
            throw new BusinessException("您未设置监督人", 404);
        }
        JobInfo jobInfo = jobInfoService.getById(param.getJobId());
        JobUser jobUser = initJobUserByReceive(nowUserInfo,jobInfo,param.getExpireTime(),param.getDesc());
        jobUserService.save(jobUser);
        return jobUser;
    }

    /**
     * 修改监督人
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/22 11:02
     */
    @Transactional(rollbackFor = Exception.class)
    public UserInfo changeWatchdog(DeleteMemberParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        if(nowUserInfo.getId().equals(param.getId())){
            throw new BusinessException("不能作为自己的监护人", 407);
        }
        UserInfo watchdog = userInfoService.getById(param.getId());
        if(watchdog == null){
            throw new BusinessException("找不到成员信息",404);
        }
        if(watchdog.getFamilyId() == null || !watchdog.getFamilyId().equals(nowUserInfo.getFamilyId())){
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        Date now = new Date();
        nowUserInfo.setWatchdogId(watchdog.getId());
        nowUserInfo.setWatchdogAvatar(watchdog.getAvatar());
        nowUserInfo.setWatchdogName(watchdog.getNickName());
        nowUserInfo.setUpdateTime(now);
        int row = userInfoService.updateRoleWithVersionLock(nowUserInfo);
        if(row == 0){
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        return nowUserInfo;
    }

    private JobUser buildJobUser(JobInfo job, UserInfo creator, UserInfo user, Date expireTime
            , String remark, BigDecimal cost, Integer canMoney, Integer canStep, Long costStep, Long points) {
        JobUser jobUser = new JobUser();
        BeanUtil.copyProperties(job, jobUser);
        jobUser.setFamilyId(creator.getFamilyId());
        Date now = new Date();
        jobUser.setCreateTime(now);
        jobUser.setUpdateTime(now);
        jobUser.setExpiredTime(expireTime);
        jobUser.setId(null);
        jobUser.setJobId(job.getId());
        jobUser.setState(BusinessConstant.JOB_USER_STATE.TO_CHECK);
        jobUser.setUserId(user.getId());
        jobUser.setUserAvatar(user.getAvatar());
        jobUser.setUserName(user.getNickName());
        jobUser.setCreatorId(creator.getId());
        jobUser.setCreatorAvatar(creator.getAvatar());
        jobUser.setCreatorName(creator.getNickName());
        jobUser.setRemark(remark);
        jobUser.setCost(cost);
        jobUser.setCanMoney(canMoney);
        jobUser.setCanStep(canStep);
        jobUser.setCostStep(costStep);
        jobUser.setCreateType(BusinessConstant.CREATE_TYPE.USER);
        jobUser.setJobNo(BusinessUtil.getOrderNumber(BusinessConstant.JOB_CODE_PREFIX));
        jobUser.setPoints(points);
        return jobUser;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<JobUser> filterAndUpdateIfExpire(List<JobUser> list) {
        List<JobUser> res = new ArrayList<>();
        List<JobUser> waitToUpdate = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (JobUser jobUser : list) {
            Integer state = jobUser.getState();
            if (BusinessConstant.JOB_USER_STATE.TO_DO == state
                    || BusinessConstant.JOB_USER_STATE.TO_CHECK == state) {
                if (isExpired(jobUser, now)) {
                    if (jobUser.getCreateType().equals(BusinessConstant.CREATE_TYPE.USER)) {
                        // 过期恢复点数
                        UserInfo userInfo = userInfoService.getById(jobUser.getCreatorId());
                        userInfo.setUpdateTime(new Date());
                        userInfo.setPoints(userInfo.getPoints() + jobUser.getPoints());
                        int row = userInfoService.updateRoleWithVersionLock(userInfo);
                        if (row == 0) {
                            // 如果更新失败  则本次先不过期
                            continue;
                        }
                        pointRecordService.createPointRecord(jobUser.getId(), jobUser.getFamilyId(), userInfo.getId()
                                , jobUser.getPoints(), BusinessConstant.POINT_RECORD_TYPE.SHOURU_DINGSHIHUIFU);
                    }
                    waitToUpdate.add(jobUser);
                    continue;
                }
            }
            res.add(jobUser);
        }
        if (CollectionUtil.isNotEmpty(waitToUpdate)) {
            jobUserService.updateBatchById(waitToUpdate);
        }
        return res;
    }

    /**
     * 检查是否过期
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/11/30 14:49
     */
    private Boolean isExpired(JobUser jobUser, Calendar now) {
        Calendar expireTime = Calendar.getInstance();
        expireTime.setTime(jobUser.getExpiredTime());
        if (expireTime.before(now)) {
            jobUser.setState(BusinessConstant.JOB_USER_STATE.EXPIRED);
            jobUser.setUpdateTime(new Date());
            return true;
        }
        return false;
    }

    public List<JobInfo> listJobInfoByType(Long typeId) {
        return jobInfoService.list(new QueryWrapper<JobInfo>().eq("type_id", typeId));
    }

    public AllJobTypeAndInfoResult listAllTypeAndInfo() {
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<JobInfo>()
                .eq("deleted", 0);
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        Long familyId = nowUserInfo.getFamilyId();
        if(familyId == null){
            queryWrapper.eq("family_id",0);
        }else {
            queryWrapper.in("family_id", 0,familyId);
        }
        List<JobInfo> allJobInfo = jobInfoService.list(queryWrapper);
        if (CollectionUtil.isEmpty(allJobInfo)) {
            return null;
        }
        List<JobType> allType = jobTypeService.list(new QueryWrapper<JobType>().eq("deleted", 0));
        if (CollectionUtil.isEmpty(allType)) {
            return null;
        }
        AllJobTypeAndInfoResult res = new AllJobTypeAndInfoResult();
        res.setJobTypeList(allType);
        res.setJobInfoList(allJobInfo);
        return res;
    }

    private JobUser changeState(UserInfo userInfo,JobUser jobUser, Integer checkState, Integer toState, boolean checkUser) {
        if (jobUser == null) {
            throw new BusinessException("找不到任务", 404);
        }
        // true-检查user false-检查creator
        if (checkUser) {
            if (!userInfo.getId().equals(jobUser.getUserId())) {
                throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
            }
        } else {
            if (!userInfo.getId().equals(jobUser.getCreatorId())) {
                throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
            }
        }
        if (!jobUser.getState().equals(checkState)) {
            throw new BusinessException(BusinessExceptionEnum.STATE_EX);
        }
        jobUser.setState(toState);
        return jobUser;
    }

    private String getStateStr(Integer state) {
        switch (state) {
            case BusinessConstant.JOB_USER_STATE.TO_CHECK:
                return "待审核";
            case BusinessConstant.JOB_USER_STATE.EXPIRED:
                return "已过期";
            case BusinessConstant.JOB_USER_STATE.REFUSED:
                return "已拒绝";
            case BusinessConstant.JOB_USER_STATE.TO_DO:
                return "待完成";
            case BusinessConstant.JOB_USER_STATE.FINISHED:
                return "已完成";
            default:
                log.error("查询到非法状态");
                throw new BusinessException(BusinessExceptionEnum.SERVER_ERROR);
        }
    }


    private JobUser initJobUserByCronJob(CronJob cronJob, JobInfo job, Date expireTime) {
        JobUser jobUser = new JobUser();
        BeanUtil.copyProperties(cronJob, jobUser);
        Date now = new Date();
        jobUser.setCreateTime(now);
        jobUser.setUpdateTime(now);
        jobUser.setId(null);
        // 定时任务直接置为待完成
        jobUser.setState(BusinessConstant.JOB_USER_STATE.TO_DO);
        jobUser.setCreatorId(cronJob.getWatchdogId());
        jobUser.setCreatorAvatar(cronJob.getWatchdogAvatar());
        jobUser.setCreatorName(cronJob.getWatchdogName());
        jobUser.setCreateType(BusinessConstant.CREATE_TYPE.CRON);
        jobUser.setJobNo(BusinessUtil.getOrderNumber(BusinessConstant.JOB_CODE_PREFIX_CRON));
        jobUser.setCronJobId(cronJob.getId());
        jobUser.setCronType(cronJob.getType());
        jobUser.setExpiredTime(expireTime);
        return jobUser;
    }

    private JobUser initJobUserByReceive(UserInfo nowUserInfo, JobInfo jobInfo, Date expireTime, String desc) {
        JobUser jobUser = new JobUser();
        jobUser.setCreateType(BusinessConstant.CREATE_TYPE.EXTRA);
        jobUser.setJobNo(BusinessUtil.getOrderNumber(BusinessConstant.JOB_CODE_PREFIX_EXTRA));
        jobUser.setPoints(jobInfo.getPoints());
        jobUser.setState(BusinessConstant.JOB_USER_STATE.TO_DO);
        Date now = new Date();
        jobUser.setCreateTime(now);
        jobUser.setUpdateTime(now);
        jobUser.setExpiredTime(expireTime);
        jobUser.setId(null);
        jobUser.setJobId(jobInfo.getId());
        jobUser.setUserId(nowUserInfo.getId());
        jobUser.setUserAvatar(nowUserInfo.getAvatar());
        jobUser.setUserName(nowUserInfo.getNickName());
        jobUser.setCreatorId(nowUserInfo.getWatchdogId());
        jobUser.setCreatorAvatar(nowUserInfo.getWatchdogAvatar());
        jobUser.setCreatorName(nowUserInfo.getWatchdogName());
        jobUser.setRemark(desc);
        jobUser.setCanMoney(0);
        jobUser.setCanStep(0);
        jobUser.setVersion(0L);
        jobUser.setTypeId(jobInfo.getTypeId());
        jobUser.setName(jobInfo.getName());
        jobUser.setFamilyId(nowUserInfo.getFamilyId());
        return jobUser;
    }


}

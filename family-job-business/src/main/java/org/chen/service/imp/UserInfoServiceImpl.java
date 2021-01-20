package org.chen.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.chen.constant.BusinessConstant;
import org.chen.dao.UserInfoMapper;
import org.chen.domain.dto.weixin.SendMsgDto;
import org.chen.domain.dto.weixin.WeixinFullUserInfoDto;
import org.chen.domain.dto.weixin.WeixinSessionDto;
import org.chen.domain.dto.weixin.WeixinUserInfoDto;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.JobUser;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.LoginByWeixinParam;
import org.chen.domain.result.*;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.manager.JobManager;
import org.chen.service.IFamilyService;
import org.chen.service.IJobUserService;
import org.chen.service.IUserInfoService;
import org.chen.timer.UpdateTimer;
import org.chen.util.BusinessUtil;
import org.chen.util.WeChatMiniProgramUtil;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {


    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private IFamilyService familyService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private IJobUserService jobUserService;

    @Autowired
    private UpdateTimer updateTimer;

    /**
     * 微信登录
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/11/26 17:29
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginByWeiXinResult loginByWeixin(LoginByWeixinParam param) {
        WeixinSessionDto weixinSessionDto = WeChatMiniProgramUtil.jsCode2Session(
                BusinessConstant.APP_ID, BusinessConstant.APP_SECRET, param.getCode());
        WeixinFullUserInfoDto fullUserInfo = param.getFullUserInfo();
        WeixinUserInfoDto weixinUserInfoDto = WeChatMiniProgramUtil.decryptUserInfo(
                fullUserInfo.getEncryptedData(), weixinSessionDto.getSession_key()
                , fullUserInfo.getIv(),WeixinUserInfoDto.class);

        if (weixinUserInfoDto == null) {
            throw new BusinessException("无法获取到用户信息", 402);
        }
        String openId = weixinUserInfoDto.getOpenId();
        if (StrUtil.isEmpty(openId)) {
            throw new BusinessException("无法获取到用户openId", 403);
        }
        // 根据openid查询
        UserInfo userInfo = getByOpenId(openId);
        // 如果第一次  则创建  否则更新
        Integer isNew;
        if (userInfo == null) {
            userInfo = createNewUser(weixinUserInfoDto);
            save(userInfo);
            isNew = 1;
        } else {
            userInfo.setAvatar(weixinUserInfoDto.getAvatarUrl());
            userInfo.setNickName(weixinUserInfoDto.getNickName());
            userInfo.setUpdateTime(new Date());
            updateById(userInfo);
            isNew = 0;
        }
        // 生成token
        String token = tokenUtil.generateToken(userInfo.getId());
        LoginByWeiXinResult res = new LoginByWeiXinResult();
        res.setIsNew(isNew);
        if (userInfo.getFamilyId() != null) {
            Family family = familyService.getById(userInfo.getFamilyId());
            res.setFamily(family);
        }
        res.setToken(token);
        res.setUserInfo(userInfo);
        return res;

    }

    /**
    * 在新事务中获取用户信息 防止重复读
    *
    * @param
    * @return
    * @author YuChen
    * @date 2020/12/16 17:45
    */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public UserInfo getUserInOtherTransaction(Long userId) {
        return getById(userId);
    }

    /**
    * 显示用户首页信息
    *
    * @param
    * @return
    * @author YuChen
    * @date 2020/12/17 9:52
    */
    @Override
    public ShowMyHomeResult homePageInfo() {
        UserInfo nowUserInfo = getNowUserInfo();
        JobCountResult jobCountResult = jobManager.jobCount();
        ShowMyHomeResult res = new ShowMyHomeResult();
        res.setMyUserInfo(nowUserInfo);
        res.setJobCountInfo(jobCountResult);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void countJobAndSendSubMsg(UserInfo userInfo) {
        List<JobUser> list = jobUserService.list(
                new QueryWrapper<JobUser>()
                        .eq("create_type", 1)
                        .eq("state", 1)
                        .eq("user_id",userInfo.getId()));
        if(CollectionUtil.isEmpty(list)){
            return;
        }
        Long points = 0L;
        Integer dayCount = 0;
        Integer weekCount = 0;
        for (JobUser jobUser:list){
            if(jobUser.getCronType().equals(BusinessConstant.CRON_TYPE.DAY)){
                dayCount ++;
            }
            if(jobUser.getCronType().equals(BusinessConstant.CRON_TYPE.WEEK)){
                weekCount ++;
            }
            points = points + jobUser.getPoints();
        }
        if(dayCount != 0 || weekCount != 0){
            String token = updateTimer.getToken();
            if(StrUtil.isEmpty(token)){
                log.warn("获取token失败,无法发送订阅消息:{}",userInfo);
                return;
            }
            SendMsgDto sendMsgDto = BusinessUtil.sendCronJobMsg(token, userInfo.getOpenId(), dayCount, weekCount, points);
            if(sendMsgDto != null){
                if(sendMsgDto.getErrcode().equals("0") || sendMsgDto.getErrcode() == null){
                    // 发送成功
                    userInfo.setCanSend(0);
                    userInfo.setUpdateTime(new Date());
                    updateRoleWithVersionLock(userInfo);
                    log.info("消息发送成功,userInfo:{}",userInfo);
                }
            }
        }
    }

    @Override
    public UserListResult userList() {
        Long userId = TokenUtil.getUserIdFromRequest();
        if (userId == null) {
            log.error("request中无法找到userId");
            throw new BusinessException(BusinessExceptionEnum.REQUEST_NO_USERID);
        }
        UserInfo userInfo = getById(userId);
        if (userInfo == null) {
            log.warn("找不到当前用户的信息，id:{}", userId);
            throw new BusinessException("找不到用户信息", 404);
        }
        Long familyId = userInfo.getFamilyId();
        if (familyId == null) {
            return null;
        }
        UserListResult res = new UserListResult();
        List<UserInfo> userList = list(new QueryWrapper<UserInfo>().eq("family_id", familyId));
        List<UserInfoResult> resultList = new ArrayList<>();
        for (UserInfo user : userList) {
            UserInfoResult userInfoResult = new UserInfoResult();
            BeanUtil.copyProperties(user, userInfoResult);
            if (user.getId().equals(userId)) {
                userInfoResult.setIsMe(1);
            } else {
                userInfoResult.setIsMe(0);
            }
            resultList.add(userInfoResult);
        }
        res.setUserList(resultList);
        return res;
    }

    @Override
    public UserInfoResult userDetail(Long userId) {
        Long myId = TokenUtil.getUserIdFromRequest();
        if (myId == null) {
            log.error("request中无法找到userId");
            throw new BusinessException(BusinessExceptionEnum.REQUEST_NO_USERID);
        }
        UserInfo myUserInfo = getById(myId);
        if (myUserInfo == null) {
            log.warn("找不到当前用户的信息，id:{}", myId);
            throw new BusinessException("找不到当前用户信息", 404);
        }
        Long familyId = myUserInfo.getFamilyId();
        if (familyId != null) {
            UserInfo userInfo = getById(userId);
            if (familyId.equals(userInfo.getFamilyId())) {
                UserInfoResult res = new UserInfoResult();
                BeanUtil.copyProperties(userInfo, res);
                if (res.getFamilyOwner() == 1) {
                    res.setIsOwnerStr("是");
                } else {
                    res.setIsOwnerStr("否");
                }
                return res;
            }
        } else {
            if (userId.equals(myId)) {
                UserInfoResult res = new UserInfoResult();
                BeanUtil.copyProperties(myUserInfo, res);
                if (res.getFamilyOwner() == 1) {
                    res.setIsOwnerStr("是");
                } else {
                    res.setIsOwnerStr("否");
                }
                return res;
            }
        }
        throw new BusinessException("不在同一家庭,无权查看该用户信息", 404);
    }


    private UserInfo createNewUser(WeixinUserInfoDto weixinUserInfoDto) {
        UserInfo userInfo = new UserInfo();
        BeanUtil.copyProperties(weixinUserInfoDto, userInfo);
        userInfo.setAvatar(weixinUserInfoDto.getAvatarUrl());
        userInfo.setFamilyOwner(0);
        userInfo.setInviteCode(BusinessUtil.getInviteCode());
        userInfo.setCreateTime(new Date());
        userInfo.setVersion(0L);
        userInfo.setPoints(0L);
        return userInfo;
    }

    private UserInfo getByOpenId(String openId) {

        return getOne(new QueryWrapper<UserInfo>().eq("open_id", openId));
    }

    public UserInfo getNowUserInfo() {
        Long userId = TokenUtil.getUserIdFromRequest();
        if (userId == null) {
            log.error("request中无法找到userId");
            throw new BusinessException(BusinessExceptionEnum.REQUEST_NO_USERID);
        }
        UserInfo user = getById(userId);
        if (user == null) {
            log.warn("找不到个人信息，id:{}", userId);
            throw new BusinessException("找不到用户信息", 404);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRoleWithVersionLock(UserInfo userInfo) {
        return userInfoMapper.updateRoleWithVersionLock(userInfo);
    }

}

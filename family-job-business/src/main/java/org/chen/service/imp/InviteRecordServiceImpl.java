package org.chen.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.constant.BusinessConstant;
import org.chen.dao.InviteRecordMapper;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.InviteRecord;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CheckInviteParam;
import org.chen.domain.param.InviteUserParam;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.service.IFamilyService;
import org.chen.service.IInviteRecordService;
import org.chen.service.IUserInfoService;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-10
 */
@Slf4j
@Service
public class InviteRecordServiceImpl extends ServiceImpl<InviteRecordMapper, InviteRecord> implements IInviteRecordService {

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IFamilyService familyService;

    @Override
    public InviteRecord getMyInviteRecord() {
        UserInfo userInfo = userInfoService.getNowUserInfo();
        QueryWrapper<InviteRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userInfo.getId())
                .eq("state",BusinessConstant.INVITE_STATE.WAIT_TO_CHECK);
        InviteRecord res = getOne(queryWrapper);
        if(res != null && !userInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)){
            log.error("成员状态错误,userinfo:{},inviterecord:{}",userInfo,res);
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InviteRecord inviteUser(InviteUserParam param) {
        UserInfo inviterInfo = userInfoService.getNowUserInfo();
        if(inviterInfo.getFamilyId() == null){
            throw new BusinessException("你还没有加入家庭",404);
        }
        Family family = familyService.getById(inviterInfo.getFamilyId());
        if(family == null){
            throw new BusinessException("家庭不存在或已被删除",404);
        }
        String inviteCode = param.getInviteCode();
        UserInfo userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>().eq("invite_code", inviteCode));
        if(userInfo == null){
            throw new BusinessException("用户不存在,请确认邀请码是否正确",404);
        }
        if(!userInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)){
            throw new BusinessException("用户已经加入家庭,无法再邀请",410);
        }
        if(userInfo.getFamilyId() != null){
            log.error("用户状态错误,userInfo:{}",userInfo);
            throw new BusinessException("用户已经加入家庭,无法再邀请",410);
        }
        userInfo.setFamilyOwner(BusinessConstant.USER_ROLE.IS_INVITING);
        userInfo.setUpdateTime(new Date());
        int row = userInfoService.updateRoleWithVersionLock(userInfo);
        if(row == 0){
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        InviteRecord res = createInviteRecord(inviterInfo,userInfo,family,param.getUserType());
        save(res);
        return res;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo acceptInvite(CheckInviteParam param) {
        return changeInviteState(param,true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo refuseInvite(CheckInviteParam param) {
        return changeInviteState(param,false);
    }

    private UserInfo changeInviteState(CheckInviteParam param ,Boolean accept){
        Long inviteRecordId = param.getInviteRecordId();
        InviteRecord record = getById(inviteRecordId);
        if(record == null){
            throw new BusinessException("找不到邀请记录",404);
        }
        if(!record.getState().equals(BusinessConstant.INVITE_STATE.WAIT_TO_CHECK)){
            throw new BusinessException("邀请状态错误",411);
        }
        Long userId = record.getUserId();
        if(!userId.equals(TokenUtil.getUserIdFromRequest())){
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        UserInfo userInfo = userInfoService.getById(userId);
        if(userInfo == null){
            throw new BusinessException("用户不存在或已删除",404);
        }
        if(userInfo.getFamilyId() != null
                || !userInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.IS_INVITING)){
            log.error("用户状态异常:userInfo:{}",userInfo);
            throw new BusinessException("用户状态异常",503);
        }
        Date now = new Date();
        userInfo.setUpdateTime(now);
        userInfo.setFamilyOwner(accept ? BusinessConstant.USER_ROLE.MEMBER : BusinessConstant.USER_ROLE.NO_FAMILY);
        if(accept){
            userInfo.setFamilyId(record.getFamilyId());
            userInfo.setUserType(record.getUserType());
            userInfo.setLastJoinTime(now);
            userInfo.setWatchdogId(record.getInviterId());
            userInfo.setWatchdogAvatar(record.getInviterAvatar());
            userInfo.setWatchdogName(record.getInviterName());
            // 如果邀请人当前没有监督人  则将邀请人的监督人设置为当前用户
            UserInfo inviter = userInfoService.getById(record.getInviterId());
            if(inviter.getWatchdogId() == null){
                inviter.setWatchdogName(userInfo.getNickName());
                inviter.setWatchdogAvatar(userInfo.getAvatar());
                inviter.setWatchdogId(userInfo.getId());
                int row = userInfoService.updateRoleWithVersionLock(inviter);
                if(row == 0){
                    throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
                }
            }
        }
        int row = userInfoService.updateRoleWithVersionLock(userInfo);
        if(row == 0){
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        record.setState(accept ? BusinessConstant.INVITE_STATE.ACCEPTED : BusinessConstant.INVITE_STATE.REFUSED);
        record.setCheckTime(now);
        updateById(record);
        return userInfo;
    }

    private InviteRecord createInviteRecord(UserInfo inviterInfo, UserInfo userInfo, Family family,String userType) {
        InviteRecord record = new InviteRecord();
        record.setInviterId(inviterInfo.getId());
        record.setUserId(userInfo.getId());
        record.setInviterAvatar(inviterInfo.getAvatar());
        record.setUserAvatar(userInfo.getAvatar());
        record.setState(BusinessConstant.INVITE_STATE.WAIT_TO_CHECK);
        record.setInviteCode(userInfo.getInviteCode());
        record.setFamilyId(family.getId());
        record.setCreateTime(new Date());
        record.setUserName(userInfo.getNickName());
        record.setInviterName(inviterInfo.getNickName());
        record.setFamilyCreatorName(family.getOwnerName());
        record.setFamilyCreatorAvatar(family.getOwnerAvatar());
        record.setFamilyName(family.getName());
        record.setUserType(userType);
        return record;
    }
}

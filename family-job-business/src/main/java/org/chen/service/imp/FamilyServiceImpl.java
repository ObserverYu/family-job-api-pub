package org.chen.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.constant.BusinessConstant;
import org.chen.dao.FamilyMapper;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CreateFamilyParam;
import org.chen.domain.param.DeleteMemberParam;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.service.IFamilyService;
import org.chen.service.IUserInfoService;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 家庭 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-25
 */
@Slf4j
@Service
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, Family> implements IFamilyService {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family createFamily(CreateFamilyParam param) {
        Long userId = TokenUtil.getUserIdFromRequest();
        UserInfo user = userInfoService.getById(userId);
        if(user == null){
            throw new BusinessException("找不到用户信息",404);
        }
        if(user.getFamilyId() != null){
            throw new BusinessException("您已存在家庭",404);
        }
        Family family = new Family();
        family.setName(param.getName());
        family.setOwner(userId);
        family.setOwnerName(user.getNickName());
        family.setOwnerAvatar(user.getAvatar());
        Date now = new Date();
        family.setCreateTime(now);
        save(family);
        user.setFamilyOwner(1);
        user.setFamilyId(family.getId());
        user.setUpdateTime(now);
        user.setLastJoinTime(now);
        userInfoService.updateById(user);
        return family;
    }

    @Override
    public Family getFamily() {
        Long userId = TokenUtil.getUserIdFromRequest();
        UserInfo user = userInfoService.getById(userId);
        if(user == null){
            throw new BusinessException("找不到用户信息",404);
        }
        if(user.getFamilyId() == null){
            return null;
        }
        return getById(user.getFamilyId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfo deleteMember(DeleteMemberParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if(nowUserInfo.getFamilyId() == null
                || !nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.OWNER)
                || nowUserInfo.getId().equals(param.getId())){
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        UserInfo deletedUser = userInfoService.getById(param.getId());
        if(deletedUser == null){
            throw new BusinessException("找不到成员信息",404);
        }
        if(deletedUser.getFamilyId() == null || !deletedUser.getFamilyId().equals(nowUserInfo.getFamilyId())){
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        Date now = new Date();
        deletedUser.setFamilyId(null);
        deletedUser.setUpdateTime(now);
        deletedUser.setFamilyOwner(BusinessConstant.USER_ROLE.NO_FAMILY);
        int row = userInfoService.updateRoleWithVersionLock(deletedUser);
        if(row == 0){
            throw new BusinessException(BusinessExceptionEnum.ROW_IS_LOCKING);
        }
        return deletedUser;
    }
}

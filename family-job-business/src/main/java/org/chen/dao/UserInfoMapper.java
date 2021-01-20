package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chen.domain.entity.UserInfo;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    int updateRoleWithVersionLock(@Param("userInfo") UserInfo userInfo);
}

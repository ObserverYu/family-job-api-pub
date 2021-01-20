package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.LoginByWeixinParam;
import org.chen.domain.result.LoginByWeiXinResult;
import org.chen.domain.result.ShowMyHomeResult;
import org.chen.domain.result.UserInfoResult;
import org.chen.domain.result.UserListResult;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface IUserInfoService extends IService<UserInfo> {

    LoginByWeiXinResult loginByWeixin(LoginByWeixinParam param);

    UserListResult userList();

    UserInfoResult userDetail(Long userId);

    UserInfo getNowUserInfo();

    int updateRoleWithVersionLock(UserInfo userInfo);

    UserInfo getUserInOtherTransaction(Long userId);

    ShowMyHomeResult homePageInfo();

    void countJobAndSendSubMsg(UserInfo userInfo);
}

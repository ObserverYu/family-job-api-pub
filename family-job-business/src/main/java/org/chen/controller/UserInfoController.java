package org.chen.controller;


import org.chen.annotion.Auth;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.DeleteMemberParam;
import org.chen.domain.param.LoginByWeixinParam;
import org.chen.domain.param.RefreshUserCanSendParam;
import org.chen.domain.result.LoginByWeiXinResult;
import org.chen.domain.result.ShowMyHomeResult;
import org.chen.domain.result.UserInfoResult;
import org.chen.domain.result.UserListResult;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.manager.JobManager;
import org.chen.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 券类 前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-11-23
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private JobManager jobManager;

    @Autowired
    private IUserInfoService userInfoService;

    @PostMapping("/loginByWeixin")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    public LoginByWeiXinResult loginByWeixin(@RequestBody LoginByWeixinParam param){
        return userInfoService.loginByWeixin(param);
    }

    @GetMapping("/homePageInfo")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public ShowMyHomeResult homePageInfo(){
        return userInfoService.homePageInfo();
    }

    @GetMapping("/getMyUserInfo")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public UserInfo getMyUserInfo(){
        return userInfoService.getNowUserInfo();
    }

    @GetMapping("/userList")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public UserListResult userList(){
        return userInfoService.userList();
    }

    @GetMapping("/userDetail")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public UserInfoResult userDetail(@RequestParam(name = "userId",required = true)Long userId){
        return userInfoService.userDetail(userId);
    }

    /**
    * 刷新给用户发送订阅消息的权限
    *
    * @param
    * @return
    * @author YuChen
    * @date 2020/12/16 17:34
    */
    @PostMapping("/freshSendMsg")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public void freshSendMsg(@RequestBody @Valid RefreshUserCanSendParam param){
        jobManager.freshSendMsg(param);
    }

    /**
     * 修改监督人
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/12/16 17:34
     */
    @PostMapping("/changeWatchdog")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public UserInfo changeWatchdog(@RequestBody @Valid DeleteMemberParam param){
        return jobManager.changeWatchdog(param);
    }
}

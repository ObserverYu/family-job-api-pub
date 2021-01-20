package org.chen.controller;


import org.chen.annotion.Auth;
import org.chen.annotion.NoRepeat;
import org.chen.domain.entity.InviteRecord;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CheckInviteParam;
import org.chen.domain.param.InviteUserParam;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.service.IInviteRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-12-10
 */
@RestController
@RequestMapping("/invite")
public class InviteRecordController {

    @Autowired
    private IInviteRecordService iInviteRecordService;

    @GetMapping("/getMyInviteRecord")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public InviteRecord getMyInviteRecord(){
        return iInviteRecordService.getMyInviteRecord();
    }

    @PostMapping("/inviteUser")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public InviteRecord inviteUser(@RequestBody @Valid InviteUserParam param){
        return iInviteRecordService.inviteUser(param);
    }

    @PostMapping("/acceptInvite")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public UserInfo acceptInvite(@RequestBody @Valid CheckInviteParam param){
        return iInviteRecordService.acceptInvite(param);
    }

    @PostMapping("/refuseInvite")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public UserInfo refuseInvite(@RequestBody @Valid CheckInviteParam param){
        return iInviteRecordService.refuseInvite(param);
    }
}

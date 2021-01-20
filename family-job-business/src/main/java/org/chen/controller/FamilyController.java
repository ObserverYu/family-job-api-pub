package org.chen.controller;


import org.chen.annotion.Auth;
import org.chen.annotion.NoRepeat;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CreateFamilyParam;
import org.chen.domain.param.DeleteMemberParam;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.service.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 家庭 前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-11-25
 */
@RestController
@RequestMapping("/family")
public class FamilyController {

    @Autowired
    private IFamilyService familyService;

    @PostMapping("/createFamily")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public Family createFamily(@RequestBody CreateFamilyParam param){
        return familyService.createFamily(param);
    }

    @GetMapping("/getFamily")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public Family getFamily(){
        return familyService.getFamily();
    }

    @PostMapping("/deleteMember")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public UserInfo deleteMember(@RequestBody DeleteMemberParam param){
        return familyService.deleteMember(param);
    }


}

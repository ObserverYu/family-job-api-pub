package org.chen.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.chen.annotion.Auth;
import org.chen.annotion.NoRepeat;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.entity.JobType;
import org.chen.domain.entity.JobUser;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.*;
import org.chen.domain.result.*;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.manager.JobManager;
import org.chen.service.IJobInfoService;
import org.chen.service.IJobTypeService;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>
 * 券信息 前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
@Controller
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Autowired
    private IJobTypeService jobTypeService;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private IJobInfoService jobInfoService;


    @GetMapping("/listAllType")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public List<JobType> listAllType() {
        QueryWrapper<JobType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        return jobTypeService.list(queryWrapper);
    }

    @GetMapping("/listAllTypeAndInfo")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public AllJobTypeAndInfoResult listAllTypeAndInfo() {
        return jobManager.listAllTypeAndInfo();
    }

    @GetMapping("/listJobInfoByType")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public List<JobInfo> listJobInfoByType(@RequestParam(required = true, name = "typeId") Long typeId) {
        return jobManager.listJobInfoByType(typeId);
    }

    @GetMapping("/jobCount")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public JobCountResult jobCount() {
        return jobManager.jobCount();
    }


    @GetMapping("/listMyJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public IPage<JobUserResult> listMyJob(@RequestParam(required = true, name = "state") Integer state
            , @RequestParam(required = true, name = "isMineJob") Integer isMineJob
            , @RequestParam(required = true, name = "pageNum")@Min(0) Integer pageNum
            , @RequestParam(required = true, name = "pageSize")@Min(1) Integer pageSize
    ) {
        Long userId = TokenUtil.getUserIdFromRequest();
        if (userId == null) {
            log.error("request中无法找到userId");
            throw new BusinessException(BusinessExceptionEnum.REQUEST_NO_USERID);
        }
        return jobManager.listMyJobUser(userId,state,isMineJob,pageNum,pageSize);
    }

    @GetMapping("/getJobUserDetail")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public JobUserResult getJobUserDetail(@RequestParam(required = true, name = "jobUserId") Long jobUserId) {
        return jobManager.getJobUserDetail(jobUserId);
    }

    @PostMapping("/createJobToUser")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public UserInfo createJobToUser(@RequestBody @Valid CreateJobToUserParam param){
        return jobManager.createJobToUser(param);
    }


    @PostMapping("/acceptJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public JobUserResult acceptJob(@RequestBody ChangeStateParam param) {
        return jobManager.acceptJob(param.getJobUserId());
    }

    @PostMapping("/refuseJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public JobUserResult refuseJob(@RequestBody  ChangeStateParam param) {
        return jobManager.refuseJob(param.getJobUserId(),param.getRefuseReason());
    }

    @PostMapping("/finishJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public JobUserResult finishJob(@RequestBody  ChangeStateParam param) {
        return jobManager.finishJob(param.getJobUserId());
    }

    @PostMapping("/finishJobByStep")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public JobUserResult finishJobByStep(@RequestBody  FinishByStepParam param) {
        return jobManager.finishJobByStep(param);
    }

    @GetMapping("/getCustomizedJobType")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public List<CustomizedJobTypeResult>  getCustomizedJobType() {
        return jobTypeService.getCustomizedJobType();
    }

    @GetMapping("/getCustomizedJobList")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public CustomizedJobListResult  getCustomizedJobList(@RequestParam(required = true, name = "typeId") Long typeId) {
        return jobTypeService.getCustomizedJobList(typeId);
    }

    @PostMapping("/deleteCustomizedJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public CustomizedJobListResult deleteCustomizedJob(@RequestBody @Valid DeleteCustomizedJobParam param) {
        return jobInfoService.deleteCustomizedJob(param);
    }

    @PostMapping("/createCustomizedJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public List<CustomizedJobTypeResult> createCustomizedJob(@RequestBody @Valid CreateCustomizedJobParam param) {
        return jobInfoService.createCustomizedJob(param);
    }

    @PostMapping("/receiveJobUser")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public JobUser receiveJobUser(@RequestBody @Valid ReceiveJobUserParam param) {
        return jobManager.receiveJobUser(param);
    }

    @GetMapping("/statistics")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public StatisticsDataResult  statistics(@RequestParam(required = true, name = "type") Integer type
            ,@RequestParam(required = true, name = "time") Integer time) {
        return jobInfoService.statistics(type,time);
    }

}

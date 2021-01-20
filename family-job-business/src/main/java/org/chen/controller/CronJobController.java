package org.chen.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.chen.annotion.Auth;
import org.chen.annotion.NoRepeat;
import org.chen.domain.param.CreateCronJobToUserParam;
import org.chen.domain.param.CronJobDetailParam;
import org.chen.domain.result.CronJobResult;
import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.manager.JobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * <p>
 * 定时任务 前端控制器
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
@RestController
@RequestMapping("/cron-job")
public class CronJobController {

    @Autowired
    private JobManager jobManager;

    @GetMapping("/listFamilyCronJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public IPage<CronJobResult> listFamilyCronJob(@RequestParam(required = true, name = "pageNum")@Min(0) Integer pageNum
            , @RequestParam(required = true, name = "pageSize")@Min(1) Integer pageSize){
        return jobManager.listFamilyCronJob(pageNum,pageSize);
    }


    @PostMapping("/createCronJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public CronJobResult createCronJob(@RequestBody @Valid CreateCronJobToUserParam param){
        return jobManager.createCronJob(param);
    }

    @GetMapping("/cronJobDetail")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    public CronJobResult cronJobDetail(@RequestParam(required = true, name = "cronJobId") Long cronJobId){
        return jobManager.cronJobDetail(cronJobId);
    }

    @PostMapping("/closeCronJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public CronJobResult closeCronJob(@RequestBody @Valid CronJobDetailParam param){
        return jobManager.closeCronJob(param);
    }


    @PostMapping("/openCronJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public CronJobResult openCronJob(@RequestBody @Valid CronJobDetailParam param){
        return jobManager.openCronJob(param);
    }


    @PostMapping("/deleteCronJob")
    @LoggingFlag(logging = true)
    @SaveRequestTimeFlag
    @ResponseBody
    @Auth
    @NoRepeat
    public CronJobResult deleteCronJob(@RequestBody @Valid CronJobDetailParam param){
        return jobManager.deleteCronJob(param);
    }

}

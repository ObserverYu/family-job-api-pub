package org.chen.timer;/*
package com.wonder.visitcontroller.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

*/

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.chen.constant.BusinessConstant;
import org.chen.domain.entity.CronJob;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.entity.JobUser;
import org.chen.manager.JobManager;
import org.chen.service.ICronJobService;
import org.chen.service.IJobInfoService;
import org.chen.service.IJobUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 定时任务类
 *
 * @author YuChen
 * @date 2019/6/13
 **/

@Slf4j
@Service
public class FamilyJobTimer {

    @Autowired
    private IJobUserService jobUserService;

    @Autowired
    private JobManager jobManager;

    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private IJobInfoService jobInfoService;

    @Autowired
    private ICronJobService cronJobService;

    private WeixinAccessToken weixinAccessToken;



    {
        threadPoolExecutor = new ThreadPoolExecutor(2, 4,
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        // 平时就超时挂起
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }



    /**
     * 每天更新过期
     *
     * @author YuChen
     * @date 2020-2-13 12:06:52
     */

    @Scheduled(cron = "0 1 0 * * ?")
    public void updateExpireJob() {
        log.info("开始过期,当前时间:{}",new Date());
        List<JobUser> list = jobUserService.list(new QueryWrapper<JobUser>()
                .in("state", BusinessConstant.JOB_USER_STATE.TO_CHECK, BusinessConstant.JOB_USER_STATE.TO_DO));
        if(CollectionUtil.isEmpty(list)){
            log.info("没有待完成和待审核的任务");
            return;
        }
        log.info("今日可能过期的任务数:{}",list.size());
        jobManager.filterAndUpdateIfExpire(list);
    }

    /**
     * 每天扫描并添加定时任务
     *
     * @author YuChen
     * @date 2020-2-13 12:06:52
     */

    @Scheduled(cron = "0 2 0 * * ?")
    public void createCronJob() {
        log.info("开始处理定时任务,当前时间:{}",new Date());
        List<CronJob> allCronJob = cronJobService.list(new QueryWrapper<CronJob>()
                .eq("state", BusinessConstant.CRON_STATE.ENABLE));
        if(CollectionUtil.isEmpty(allCronJob)){
            log.info("定时任务为空");
            return;
        }
        log.info("当前需处理的定时任务数:{}",allCronJob.size());
        List<JobInfo> jobInfoList = jobInfoService.list();
        if(CollectionUtil.isEmpty(jobInfoList)){
            log.error("数据错误!没有jobinfo!");
            return;
        }
        Map<Long, JobInfo> jobInfoCache = jobInfoList.stream().collect(Collectors.toMap(JobInfo::getId, j -> j));
        for(CronJob cronJob:allCronJob){
            CreateCronJobTask task = new CreateCronJobTask(cronJob, this.jobManager, jobInfoCache);
            this.threadPoolExecutor.submit(task);
        }
    }




    private static class CreateCronJobTask implements Runnable{

        private CronJob cronJob;

        private JobManager jobManager;

        Map<Long, JobInfo> jobInfoCache;

        public CreateCronJobTask(CronJob cronJob, JobManager jobManager, Map<Long, JobInfo> jobInfoCache) {
            this.cronJob = cronJob;
            this.jobManager = jobManager;
            this.jobInfoCache = jobInfoCache;
        }

        @Override
        public void run() {
            try{
                JobInfo jobInfo = jobInfoCache.get(cronJob.getJobId());
                jobManager.createByCronJob(this.cronJob,jobInfo,false);
            }catch (Exception e){
                log.warn("初始化定时任务失败,cronjob:{}",this.cronJob);
                log.warn("初始化定时任务失败",e);
            }
        }
    }

    private static class WeixinAccessToken{
        String token;

        Date updateDate;
    }

}

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
import org.chen.domain.entity.UserInfo;
import org.chen.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务类
 *
 * @author YuChen
 * @date 2019/6/13
 **/

@Slf4j
@Service
public class WeixinSubMsgTimer {


    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private IUserInfoService userInfoService;


    {
        threadPoolExecutor = new ThreadPoolExecutor(2, 4,
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        // 平时就超时挂起
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }


    /**
     * 每天扫描并发送消息
     *
     * @author YuChen
     * @date 2020-2-13 12:06:52
     */

    @Scheduled(cron = "0 20 8 * * ?")
    public void createCronJob() {
        log.info("开始发送订阅消息,当前时间:{}",new Date());
        List<UserInfo> list = userInfoService.list(new QueryWrapper<UserInfo>().eq("can_send",1));
        if(CollectionUtil.isEmpty(list)){
            log.info("没有可发送定时消息的用户");
            return;
        }
        log.info("当前可发送消息的用户数:{}",list.size());
        for(UserInfo userInfo:list){
            SenWeiXinSubMsgTask task = new SenWeiXinSubMsgTask(userInfo,this.userInfoService);
            this.threadPoolExecutor.submit(task);
        }
    }


    private static class SenWeiXinSubMsgTask implements Runnable{

        private UserInfo userInfo;

        private IUserInfoService userInfoService;

        public SenWeiXinSubMsgTask(){}

        public SenWeiXinSubMsgTask(UserInfo userInfo, IUserInfoService userInfoService) {
            this.userInfo = userInfo;
            this.userInfoService = userInfoService;
        }

        @Override
        public void run() {
            try{
                userInfoService.countJobAndSendSubMsg(this.userInfo);
            }catch (Exception e){
                log.warn("发送订阅消息出现异常,userInfo:{}",this.userInfo);
                log.warn("发送订阅消息出现异常",e);
            }
        }
    }


}

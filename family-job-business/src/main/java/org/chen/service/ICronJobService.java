package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.CronJob;

/**
 * <p>
 * 定时任务 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
public interface ICronJobService extends IService<CronJob> {
    int updateCronJobWithLock(CronJob cronJob);
}

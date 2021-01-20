package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chen.domain.entity.CronJob;

/**
 * <p>
 * 定时任务 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
public interface CronJobMapper extends BaseMapper<CronJob> {

    int updateCronJobWithLock(@Param("cronJob") CronJob cronJob);
}

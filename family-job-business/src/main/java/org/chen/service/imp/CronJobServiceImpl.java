package org.chen.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.CronJobMapper;
import org.chen.domain.entity.CronJob;
import org.chen.service.ICronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 定时任务 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
@Slf4j
@Service
public class CronJobServiceImpl extends ServiceImpl<CronJobMapper, CronJob> implements ICronJobService {

    @Autowired
    private CronJobMapper cronJobMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCronJobWithLock(CronJob cronJob) {
        return cronJobMapper.updateCronJobWithLock(cronJob);
    }
}

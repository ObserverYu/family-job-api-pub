package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.StepRecord;

/**
 * <p>
 * 步数记录 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-24
 */
public interface IStepRecordService extends IService<StepRecord> {

    StepRecord getYesterdayRecord(Long userId);

    int updateRemainingStepWithLock(StepRecord stepRecord);

    void saveInOtherTransaction(StepRecord yesterdayRecord);
}

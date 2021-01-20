package org.chen.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.StepRecordMapper;
import org.chen.domain.entity.StepRecord;
import org.chen.service.IStepRecordService;
import org.chen.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 步数记录 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-12-24
 */
@Slf4j
@Service
public class StepRecordServiceImpl extends ServiceImpl<StepRecordMapper, StepRecord> implements IStepRecordService {

    @Autowired
    private StepRecordMapper stepRecordMapper;

    @Override
    public StepRecord getYesterdayRecord(Long userId) {
        String yesterdayDate = DateUtils.getYesterdayDateStr();
        QueryWrapper<StepRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("start_date",yesterdayDate);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRemainingStepWithLock(StepRecord stepRecord) {
       return stepRecordMapper.updateRemainingStepWithLock(stepRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void saveInOtherTransaction(StepRecord yesterdayRecord) {
        save(yesterdayRecord);
    }

}

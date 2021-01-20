package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chen.domain.entity.StepRecord;

/**
 * <p>
 * 步数记录 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-12-24
 */
public interface StepRecordMapper extends BaseMapper<StepRecord> {

    int updateRemainingStepWithLock(@Param("stepRecord") StepRecord stepRecord);
}

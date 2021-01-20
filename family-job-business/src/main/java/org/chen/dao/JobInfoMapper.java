package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.result.StatisticsResult;

import java.util.List;

/**
 * <p>
 * 券信息 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface JobInfoMapper extends BaseMapper<JobInfo> {

    List<StatisticsResult> statisticsNum(@Param("start") String start, @Param("end") String end
            , @Param("familyId") Long familyId);

    List<StatisticsResult> statisticsPoint(@Param("start") String start, @Param("end") String end
            , @Param("familyId") Long familyId);

    List<StatisticsResult> statisticsItem(@Param("start") String start, @Param("end") String end
            , @Param("familyId") Long familyId);
}

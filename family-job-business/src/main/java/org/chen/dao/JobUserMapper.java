package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.chen.domain.dto.JobUserCountDto;
import org.chen.domain.entity.JobUser;

import java.util.List;

/**
 * <p>
 * 券信息 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface JobUserMapper extends BaseMapper<JobUser> {

    List<JobUserCountDto> jobCount(@Param("userId") Long userId);

    int updateStateWithVersion(@Param("jobUser") JobUser jobUser);
}

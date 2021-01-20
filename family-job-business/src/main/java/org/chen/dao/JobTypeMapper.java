package org.chen.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.chen.domain.dto.CustomizedJobCountDto;
import org.chen.domain.entity.JobType;

import java.util.List;

/**
 * <p>
 * 券类 Mapper 接口
 * </p>
 *
 * @author YuChen
 * @since 2020-11-23
 */
public interface JobTypeMapper extends BaseMapper<JobType> {

    List<CustomizedJobCountDto> countCustomizedJob(Long familyId);
}

package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.JobType;
import org.chen.domain.result.CustomizedJobListResult;
import org.chen.domain.result.CustomizedJobTypeResult;

import java.util.List;

/**
 * <p>
 * 券类 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-23
 */
public interface IJobTypeService extends IService<JobType> {

    List<CustomizedJobTypeResult> getCustomizedJobType();

    CustomizedJobListResult getCustomizedJobList(Long typeId);

}

package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.param.CreateCustomizedJobParam;
import org.chen.domain.param.DeleteCustomizedJobParam;
import org.chen.domain.result.CustomizedJobListResult;
import org.chen.domain.result.CustomizedJobTypeResult;
import org.chen.domain.result.StatisticsDataResult;

import java.util.List;

/**
 * <p>
 * 券信息 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface IJobInfoService extends IService<JobInfo> {

    CustomizedJobListResult deleteCustomizedJob(DeleteCustomizedJobParam param);

    List<CustomizedJobTypeResult> createCustomizedJob(CreateCustomizedJobParam param);

    StatisticsDataResult statistics(Integer type, Integer time);
}

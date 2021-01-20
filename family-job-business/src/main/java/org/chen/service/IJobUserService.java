package org.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chen.domain.dto.JobUserCountDto;
import org.chen.domain.entity.JobUser;

import java.util.List;

/**
 * <p>
 * 券信息 服务类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
public interface IJobUserService extends IService<JobUser> {

    List<JobUserCountDto> jobCount(Long userId);

    JobUser getInOtherTransaction(Long jobUserId);

    int updateStateWithVersion(JobUser jobUser);

}

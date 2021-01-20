package org.chen.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.JobUserMapper;
import org.chen.domain.dto.JobUserCountDto;
import org.chen.domain.entity.JobUser;
import org.chen.service.IJobUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 券信息 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
@Slf4j
@Service
public class JobUserServiceImpl extends ServiceImpl<JobUserMapper, JobUser> implements IJobUserService {

    @Autowired
    private JobUserMapper jobUserMapper;

    @Override
    public List<JobUserCountDto> jobCount(Long userId) {
        return jobUserMapper.jobCount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public JobUser getInOtherTransaction(Long jobUserId) {
        return jobUserMapper.selectById(jobUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStateWithVersion(JobUser jobUser) {
        return jobUserMapper.updateStateWithVersion(jobUser);
    }
}

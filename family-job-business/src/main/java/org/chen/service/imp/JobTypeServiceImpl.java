package org.chen.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.dao.JobTypeMapper;
import org.chen.domain.dto.CustomizedJobCountDto;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.entity.JobType;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.result.CustomizedJobListResult;
import org.chen.domain.result.CustomizedJobTypeResult;
import org.chen.framework.businessex.BusinessException;
import org.chen.service.IJobInfoService;
import org.chen.service.IJobTypeService;
import org.chen.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 券类 服务实现类
 * </p>
 *
 * @author YuChen
 * @since 2020-11-23
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JobTypeServiceImpl extends ServiceImpl<JobTypeMapper, JobType> implements IJobTypeService {

    @Autowired
    private JobTypeMapper jobTypeMapper;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IJobInfoService jobInfoService;


    @Override
    public List<CustomizedJobTypeResult> getCustomizedJobType() {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if( nowUserInfo.getFamilyId() == null){
            throw new BusinessException("你还没有加入家庭", 404);
        }
        List<JobType> allJobType = list(new QueryWrapper<JobType>().eq("deleted", 0));
        if(CollectionUtil.isEmpty(allJobType)){
            return null;
        }
        List<CustomizedJobCountDto> customizedJobCountDto = jobTypeMapper.countCustomizedJob(nowUserInfo.getFamilyId());
        Map<Long, CustomizedJobCountDto> typeIdMapEntity = null;
        if(CollectionUtil.isNotEmpty(customizedJobCountDto)){
            typeIdMapEntity = customizedJobCountDto.stream().collect(Collectors.toMap(CustomizedJobCountDto::getTypeId, c -> c));
        }
        List<CustomizedJobTypeResult> res = new ArrayList<>();
        for(JobType jobType:allJobType){
            CustomizedJobTypeResult jobTypeRes = new CustomizedJobTypeResult();
            BeanUtil.copyProperties(jobType,jobTypeRes);
            Integer count = 0;
            if(typeIdMapEntity != null){
                CustomizedJobCountDto countDto = typeIdMapEntity.get(jobType.getId());
                if(countDto != null){
                    count = countDto.getCount();
                }
            }
            jobTypeRes.setCount(count);
            res.add(jobTypeRes);
        }
        return res;
    }

    @Override
    public CustomizedJobListResult getCustomizedJobList(Long typeId) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if( nowUserInfo.getFamilyId() == null){
            throw new BusinessException("你还没有加入家庭", 404);
        }
        JobType jobType = getById(typeId);
        if(jobType == null){
            throw new BusinessException("该类别不存在", 404);
        }
        List<JobInfo> jobList = jobInfoService.list(new QueryWrapper<JobInfo>()
                .eq("deleted", 0)
                .eq("family_id", nowUserInfo.getFamilyId())
                .eq("type_id",typeId));

        CustomizedJobListResult res = new CustomizedJobListResult();
        res.setTypeId(typeId);
        res.setTypeName(jobType.getName());
        if(CollectionUtil.isNotEmpty(jobList)){
            res.setJobList(jobList);
        }
        return res;

    }
}

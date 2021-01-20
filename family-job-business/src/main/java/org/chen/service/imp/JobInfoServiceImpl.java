package org.chen.service.imp;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.chen.constant.BusinessConstant;
import org.chen.dao.JobInfoMapper;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.entity.JobType;
import org.chen.domain.entity.UserInfo;
import org.chen.domain.param.CreateCustomizedJobParam;
import org.chen.domain.param.DeleteCustomizedJobParam;
import org.chen.domain.result.CustomizedJobListResult;
import org.chen.domain.result.CustomizedJobTypeResult;
import org.chen.domain.result.StatisticsDataResult;
import org.chen.domain.result.StatisticsResult;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.service.IJobInfoService;
import org.chen.service.IJobTypeService;
import org.chen.service.IUserInfoService;
import org.chen.timer.UpdateTimer;
import org.chen.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public class JobInfoServiceImpl extends ServiceImpl<JobInfoMapper, JobInfo> implements IJobInfoService {

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private IJobTypeService jobTypeService;

    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomizedJobListResult deleteCustomizedJob(DeleteCustomizedJobParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        if (!nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.OWNER)) {
            throw new BusinessException("只有家长能删除自定义家务项", 407);
        }
        JobInfo jobInfo = getById(param.getJobInfoId());
        if(jobInfo == null){
            throw new BusinessException("家务不存在或已删除", 404);
        }
        if(!jobInfo.getFamilyId().equals(nowUserInfo.getFamilyId())){
            throw new BusinessException(BusinessExceptionEnum.NO_AUTH);
        }
        jobInfo.setUpdateTime(new Date());
        jobInfo.setDeleted(1);
        updateById(jobInfo);
        return jobTypeService.getCustomizedJobList(jobInfo.getTypeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CustomizedJobTypeResult> createCustomizedJob(CreateCustomizedJobParam param) {
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if (nowUserInfo.getFamilyId() == null || nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.NO_FAMILY)) {
            throw new BusinessException("你还没有加入家庭", 404);
        }
        if (!nowUserInfo.getFamilyOwner().equals(BusinessConstant.USER_ROLE.OWNER)) {
            throw new BusinessException("只有家长能创建自定义家务项", 407);
        }
        JobType jobType = jobTypeService.getById(param.getTypeId());
        if(jobType == null || jobType.getDeleted() == 1){
            throw new BusinessException("该类别不存在或已删除", 404);
        }
        JobInfo jobInfo = buildCustomizedJob(param.getJobName(),param.getTypeId(),nowUserInfo.getFamilyId(),param.getPoints());
        save(jobInfo);
        return jobTypeService.getCustomizedJobType();
    }

    /**
    * 获取统计数据
    *
    * @param
    * @return
    * @author YuChen
    * @date 2020/12/23 10:56
    */
    @Override
    public StatisticsDataResult statistics(Integer type, Integer time) {
        String timeStr = BusinessConstant.STATISTICS_TIME.MAP.get(time);
        if(timeStr == null){
            throw new BusinessException("参数错误:time",414);
        }
        String typeStr = BusinessConstant.STATISTICS_TYPE.MAP.get(type);
        if(typeStr == null){
            throw new BusinessException("参数错误:type",414);
        }
        UserInfo nowUserInfo = userInfoService.getNowUserInfo();
        if(nowUserInfo.getFamilyId() == null){
            return null;
        }
        String start = null;
        String end = null;
        List<StatisticsResult> showData = null;
        String title = "";
        switch (time){
            case BusinessConstant.STATISTICS_TIME.WEEK:
                start = DateUtils.format(DateUtils.getCurrentWeekDayStartTime(),DateUtils.FORMAT_yyyy_MM_dd_HH_mm_ss);
                end = DateUtils.format(DateUtils.getCurrentWeekDayEndTime(),DateUtils.FORMAT_yyyy_MM_dd_HH_mm_ss);
                break;
            case BusinessConstant.STATISTICS_TIME.MONTH:
                start = DateUtils.format(DateUtils.getCurrentMonthStartTime(),DateUtils.FORMAT_yyyy_MM_dd_HH_mm_ss);
                end = DateUtils.format(DateUtils.getCurrentMonthEndTime(),DateUtils.FORMAT_yyyy_MM_dd_HH_mm_ss);
                break;
            default:
                break;
        }

        switch (type){
            case BusinessConstant.STATISTICS_TYPE.NUM:
                showData = statisticsNum(start,end,nowUserInfo);
                title = "完成家务数";
                break;
            case BusinessConstant.STATISTICS_TYPE.POINT:
                showData = statisticsPoint(start,end,nowUserInfo);
                title = "获得家务点(不含指派)";
                break;
            case BusinessConstant.STATISTICS_TYPE.ITEM:
                showData = statisticsItem(start,end,nowUserInfo);
                title = "不同家务占比";
                break;
            default:
                break;
        }
        showData = CollectionUtil.isEmpty(showData)?null:showData;
        StatisticsDataResult res = new StatisticsDataResult();
        res.setItemList(UpdateTimer.getStatisticsItemList());
        res.setShowData(showData);
        res.setTitle(title);
        return res;
    }

    private List<StatisticsResult> statisticsItem(String start, String end, UserInfo nowUserInfo) {
        return jobInfoMapper.statisticsItem(start,end,nowUserInfo.getFamilyId());
    }

    private List<StatisticsResult> statisticsPoint(String start, String end, UserInfo nowUserInfo) {
        return jobInfoMapper.statisticsPoint(start,end,nowUserInfo.getFamilyId());
    }

    private List<StatisticsResult> statisticsNum(String start, String end, UserInfo nowUserInfo) {
        return jobInfoMapper.statisticsNum(start,end,nowUserInfo.getFamilyId());
    }

    private JobInfo buildCustomizedJob(String jobName, Long typeId, Long familyId, Long points) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setDeleted(0);
        Date now = new Date();
        jobInfo.setUpdateTime(now);
        jobInfo.setCreateTime(now);
        jobInfo.setFamilyId(familyId);
        jobInfo.setTypeId(typeId);
        jobInfo.setName(jobName);
        jobInfo.setPoints(points);
        return jobInfo;
    }
}

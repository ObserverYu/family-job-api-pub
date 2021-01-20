package org.chen.domain.result;

import lombok.Data;
import org.chen.domain.entity.JobInfo;
import org.chen.domain.entity.JobType;

import java.util.List;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class AllJobTypeAndInfoResult {
    List<JobType> jobTypeList;

    List<JobInfo> jobInfoList;

}

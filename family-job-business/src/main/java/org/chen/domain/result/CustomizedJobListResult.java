package org.chen.domain.result;

import lombok.Data;
import org.chen.domain.entity.JobInfo;

import java.util.List;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class CustomizedJobListResult {

    private String typeName;

    /**
     * 0-其他
     */
    private Long typeId;


    private List<JobInfo> jobList;


}

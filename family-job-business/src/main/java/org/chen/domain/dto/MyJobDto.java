package org.chen.domain.dto;

import lombok.Data;
import org.chen.domain.entity.JobUser;

import java.util.List;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class MyJobDto {

    private Integer count;

    private Long jobId;

    private String name;

    private Long familyId;

    private Integer deleted;

    private Integer personal;

    List<JobUser> jobUserList;
}

package org.chen.domain.dto;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class JobUserCountDto {
    Integer state;
    Long userId;
    Long count;
    Long creatorId;
}

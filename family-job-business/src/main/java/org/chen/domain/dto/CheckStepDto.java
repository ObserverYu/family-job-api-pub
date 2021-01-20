package org.chen.domain.dto;

import lombok.Data;
import org.chen.domain.entity.StepRecord;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class CheckStepDto {

    private Boolean canFinish;

    private StepRecord record;
}

package org.chen.domain.dto.weixin;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/18 12:02
 **/

@Data
@ToString
public class WxRunDataDto {

    private List<StepInfo>  stepInfoList;


    @ToString
    public static class StepInfo{
        Long timestamp;
        Long step;

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Long getStep() {
            return step;
        }

        public void setStep(Long step) {
            this.step = step;
        }
    }
}

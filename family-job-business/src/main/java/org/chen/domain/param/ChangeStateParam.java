package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class ChangeStateParam {

    @NotEmpty
    private Long jobUserId;

    private Long costStep;

    private String refuseReason;

}

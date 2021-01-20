package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class FinishByStepParam {

    @NotNull
    private Long jobUserId;

    @NotEmpty
    private String code;

    @NotEmpty
    private String encryptedData;

    @NotEmpty
    private String iv;

}

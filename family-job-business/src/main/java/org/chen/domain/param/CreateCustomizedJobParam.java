package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class CreateCustomizedJobParam {

    @NotNull
    private Long typeId;

    @NotEmpty
    private String jobName;

    @NotNull
    private Long points;


}

package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class CreateCronJobToUserParam {

    @NotNull
    private Long userId;

    @NotNull
    private Long jobId;

    @NotNull
    private Long points;

    @NotNull
    private Integer times;

    //0-每周,1-每日,2-每月
    @NotNull
    private Integer type;

    private String remark;

    private Integer canStep;

    private Long costStep;

    private Integer enableNow;
}

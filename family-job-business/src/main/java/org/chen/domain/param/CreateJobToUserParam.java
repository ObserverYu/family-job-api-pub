package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class CreateJobToUserParam {

    @NotNull
    private Long userId;

    @NotNull
    private Long jobId;

    @NotNull
    private Date expireTime;

    private String desc;

    private Integer  canMoney;
    private BigDecimal cost;


    private Integer  canStep;
    private Long  costStep;

    @NotNull
    private Long  points;
}

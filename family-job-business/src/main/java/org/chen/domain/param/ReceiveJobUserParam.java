package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class ReceiveJobUserParam {

    @NotNull
    private Long jobId;

    @NotNull
    private Date expireTime;

    private String desc;
}

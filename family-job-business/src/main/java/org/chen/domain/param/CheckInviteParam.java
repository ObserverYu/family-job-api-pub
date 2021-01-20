package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class CheckInviteParam {

    @NotNull
    private Long inviteRecordId;

}

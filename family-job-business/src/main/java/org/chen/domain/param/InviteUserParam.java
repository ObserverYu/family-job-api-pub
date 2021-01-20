package org.chen.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class InviteUserParam {

    @NotEmpty
    private String inviteCode;

    private String userType;

}

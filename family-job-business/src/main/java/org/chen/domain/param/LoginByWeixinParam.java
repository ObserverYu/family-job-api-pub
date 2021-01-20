package org.chen.domain.param;

import lombok.Data;
import org.chen.domain.dto.weixin.WeixinFullUserInfoDto;

import javax.validation.constraints.NotEmpty;

/**
 * @author ObserverYu
 * @date 2020/11/24 10:21
 **/

@Data
public class LoginByWeixinParam {

    @NotEmpty
    private String code;

    private WeixinFullUserInfoDto fullUserInfo;

}

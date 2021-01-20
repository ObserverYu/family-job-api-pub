package org.chen.domain.dto.weixin;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/18 12:02
 **/

@Data
public class GetAccessTokenDto {

    private String access_token;
    private Long expires_in;
    private String errcode;
    private String errmsg;
}

package org.chen.domain.dto.weixin;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/26 17:39
 **/

@Data
public class WeixinSessionDto {

    private String openid;
    private String session_key;
    private String unionid;
    private String errcode;
    private String errmsg;
}

package org.chen.domain.dto.weixin;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/26 17:05
 **/

@Data
public class WeixinFullUserInfoDto {

    private WeixinUserInfoDto userInfo;

    private String rawData;

    private String signature;

    private String encryptedData;

    private String iv;

    private String cloudID;
}

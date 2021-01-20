package org.chen.domain.result;

import lombok.Data;
import org.chen.domain.entity.Family;
import org.chen.domain.entity.UserInfo;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class LoginByWeiXinResult {

    UserInfo userInfo;

    String token;

    Family family;

    Integer isNew;
}

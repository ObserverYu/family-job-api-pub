package org.chen.property.redisson;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/4/17 10:51
 **/

@Data
@ToString
public class RedisSingleProperties {
    private  String address;
    private  String password;
}

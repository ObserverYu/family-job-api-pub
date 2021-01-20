package org.chen.property.redisson;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/4/17 10:50
 **/

@Data
@ToString
public class RedisPoolProperties {

    private int maxIdleSize;

    private int minIdleSize;
//
//    private int maxActive;
//
//    private int maxWait;
//
//    private int connTimeout;
//
//    private int soTimeout;

    /**
     * 池大小
     */
    private  int size;

}

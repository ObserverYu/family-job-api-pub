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
public class RedisClusterProperties {


    /**
     * 集群节点
     */
    private String[] nodes;
    private String password;

}

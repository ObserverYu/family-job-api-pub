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
public class RedisSentinelProperties {

    /**
     * 哨兵master 名称
     */
    private String master;

    /**
     * 哨兵节点
     */
    private String nodes;
    private String password;

}

package org.chen.property.redisson;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/4/17 10:38
 **/

@ConfigurationProperties(prefix = "spring.redis", ignoreUnknownFields = true)
@Data
@ToString
@Component
public class RedisProperties {

    private int database;

    private String mode;

    /**
     * 池配置
     */
    private RedisPoolProperties pool;

    /**
     * 单机信息配置
     */
    private RedisSingleProperties single;


    /**
     * 哨兵配置
     */
    private RedisSentinelProperties sentinel;

    private RedisClusterProperties cluster;
}
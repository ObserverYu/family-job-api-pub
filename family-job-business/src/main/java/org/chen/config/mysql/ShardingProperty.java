package org.chen.config.mysql;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/8/21 11:27
 **/

@ConditionalOnProperty(prefix = "spring.datasource",name = "mode",havingValue = "sharding")
@Data
@ConfigurationProperties(value = "spring.datasource",ignoreUnknownFields = true)
@Component
public class ShardingProperty {
    ShardingDSProperty[] sharding;
}

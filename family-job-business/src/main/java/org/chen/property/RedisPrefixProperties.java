package org.chen.property;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * redisToken配置类
 *
 * @author LiYuan
 * @date 2019/1/15
 **/
@ConfigurationProperties(prefix = "redisprefix", ignoreUnknownFields = true)
@Data
@ToString
@Component
public class RedisPrefixProperties {

    /**
     * redis保存token的key
     *
     */
    String tokenPrefix;

    /**
     * 过期时间  单位：天
     *
     */
    Long tokenExpireTime;

    String tokenHeader;

    String noRepeatPrefix;

    Long noRepeatExpireTime;

    String weixinTokenPrefix;
    String weixinTokenLock;

}

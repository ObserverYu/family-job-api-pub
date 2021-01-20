package org.chen.config.redis;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.chen.property.redisson.RedisClusterProperties;
import org.chen.property.redisson.RedisProperties;
import org.chen.property.redisson.RedisSentinelProperties;
import org.chen.property.redisson.RedisSingleProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author YuChen
 * @date 2020/4/17 10:37
 **/

@Configuration
@Slf4j
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @ConditionalOnProperty(prefix = "spring.redis",name = "enable",havingValue = "true")
    RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        String mode = redisProperties.getMode();
        log.info("redis pool config" + redisProperties.getPool());
        if ("sentinel".equals(mode)) {
            RedisSentinelProperties sentinel = redisProperties.getSentinel();
            log.info("use sentinel redisProperties:" + sentinel);
            String[] nodes = redisProperties.getSentinel().getNodes().split(",");
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach((index) -> newNodes.add(
                    index.startsWith("redis://") ? index : "redis://" + index));
            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(newNodes.toArray(new String[0]))
                    .setMasterName(sentinel.getMaster())
                    .setMasterConnectionPoolSize(redisProperties.getPool().getSize())
                    .setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMinIdleSize())
                    .setSlaveConnectionPoolSize(redisProperties.getPool().getSize())
                    .setSlaveConnectionMinimumIdleSize(redisProperties.getPool().getMinIdleSize());
            if (StrUtil.isNotBlank(sentinel.getPassword())) {
                serverConfig.setPassword(sentinel.getPassword());
            }
        } else if("cluster".equals(mode)){
            RedisClusterProperties cluster = redisProperties.getCluster();
            String[] nodes = cluster.getNodes();
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach((index) -> newNodes.add(
                    index.startsWith("redis://") ? index : "redis://" + index));
            ClusterServersConfig clusterServersConfig = config.useClusterServers()
                    .addNodeAddress(newNodes.toArray(new String[0]))
                    .setMasterConnectionPoolSize(redisProperties.getPool().getSize())
                    .setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMinIdleSize())
                    .setSlaveConnectionPoolSize(redisProperties.getPool().getSize())
                    .setSlaveConnectionMinimumIdleSize(redisProperties.getPool().getMinIdleSize());
            String password = cluster.getPassword();
            if(StrUtil.isNotBlank(password)){
                //设置密码
                clusterServersConfig.setPassword(cluster.getPassword());
            }
            return Redisson.create(config);

        } else {
            RedisSingleProperties single = redisProperties.getSingle();
            log.info("use single redisProperties:" + single);
            String node = single.getAddress();
            node = node.startsWith("redis://") ? node : "redis://" + node;
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(node)
                    .setConnectionMinimumIdleSize(redisProperties.getPool().getMinIdleSize())
                    .setConnectionPoolSize(redisProperties.getPool().getSize());
            if (StrUtil.isNotBlank(single.getPassword())) {
                serverConfig.setPassword(single.getPassword());
            }
        }
        return Redisson.create(config);
    }
}

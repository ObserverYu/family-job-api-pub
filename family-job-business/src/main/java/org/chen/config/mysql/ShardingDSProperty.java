package org.chen.config.mysql;

import lombok.Data;

import java.util.Properties;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/8/21 11:27
 **/

@Data
public class ShardingDSProperty {

    private String dsname;
    private String username;
    private String password;
    private String driverClassName;
    private String url;
    private String platform;
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;
    private Integer timeBetweenEvictionRunsMillis;
    private Integer minEvictableIdleTimeMillis;
    private String validationQuery;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean poolPreparedStatements;
    private Integer maxPoolPreparedStatementPerConnectionSize;
    private String filters;
    private Properties connectionProperties;
    private Boolean useGlobalDataSourceStat;
}

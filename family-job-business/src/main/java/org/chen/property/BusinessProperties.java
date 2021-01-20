package org.chen.property;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/4/17 10:38
 **/

@ConfigurationProperties(prefix = "business", ignoreUnknownFields = true)
@Data
@ToString
@Component
public class BusinessProperties {

    private String huidaoHost;
    private String huidaoGateway;

    private OtherEnvProperties[] other;

    public Map<String,String> getOtherPropertiesMap(){
        if(other == null || other.length == 0){
            return null;
        }
        Map<String,String> res = new HashMap<>();
        for(OtherEnvProperties entity:other){
            res.put(entity.getKey(),entity.getValue());
        }
        return res;
    }

    /**
     * 池配置
     */
    private DefaultServiceProperties service;

}
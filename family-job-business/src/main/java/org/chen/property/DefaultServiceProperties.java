package org.chen.property;


import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/4/17 10:38
 **/
@Data
@ToString
public class DefaultServiceProperties {
    private String appId;
    private String appKey;

    private ApiProperties[] apiIds;

    public Map<String,String> getApiIdsMap(){
        if(apiIds == null || apiIds.length == 0){
            return null;
        }
        Map<String,String> res = new HashMap<>();
        for(ApiProperties api:apiIds){
            res.put(api.getName(),api.getValue());
        }
        return res;
    }
}
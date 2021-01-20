package org.chen.domain.dto.weixin;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/18 12:02
 **/

@Data
public class SendMsgParamDto {


    /**
     * touser : OPENID
     * template_id : TEMPLATE_ID
     * page : index
     * miniprogram_state : developer
     * data : {"number01":{"value":"339208499"},"date01":{"value":"2015年01月05日"},"site01":{"value":"TIT创意园"},"site02":{"value":"广州市新港中路397号"}}
     */

    private String touser;
    private String template_id;
    private String page;
    private String miniprogram_state;
    private JSONObject data;
}

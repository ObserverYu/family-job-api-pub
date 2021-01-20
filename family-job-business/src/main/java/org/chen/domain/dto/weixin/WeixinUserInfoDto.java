package org.chen.domain.dto.weixin;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/26 17:05
 **/

@Data
@ToString
public class WeixinUserInfoDto {
    private String country;
    private String unionId;
    private WatermarkEntity watermark;
    private int gender;
    private String province;
    private String city;
    private String avatarUrl;
    private String openId;
    private String nickName;
    private String language;

    public class WatermarkEntity {
        /**
         * appid : wxc1a151d84acc4741
         * timestamp : 1557151075
         */
        private String appid;
        private int timestamp;

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public int getTimestamp() {
            return timestamp;
        }
    }

}

package org.chen.domain.result;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class UserInfoResult {
    private Long id;

    private String openId;

    private String unionId;

    /**
     * 微信名字
     */
    private String nickName;

    /**
     * 家庭成员角色  随意定义即可
     */
    private String userType;

    /**
     * 家庭id
     */
    private Long familyId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 我的邀请码
     */
    private String inviteCode;

    /**
     * 0-暂未加入 1-拥有者  2-成员
     */
    private Integer familyOwner;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String city;

    private String province;

    private String gender;

    private String country;

    private String mobile;
    private Date lastJoinTime;
    private Long version;
    private Long watchdogId;
    private String watchdogName;
    private String watchdogAvatar;
    private Long points;
    private Integer canSend;

    private Integer isMe;
    private String isOwnerStr;
    private String ownerTypeStr;
    private JSONObject canSendEntity;
}

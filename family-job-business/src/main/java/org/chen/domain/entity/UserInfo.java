package org.chen.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author YuChen
 * @since 2020-11-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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


}

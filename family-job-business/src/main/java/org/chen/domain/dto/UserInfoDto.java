package org.chen.domain.dto;

import lombok.Data;
import org.chen.domain.entity.Family;

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
public class UserInfoDto {

    private Long id;

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
    private Integer familyId;

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

    private Family family;

    private Long watchdogId;
    private String watchdogName;
    private String watchdogAvatar;
    private Long points;



}

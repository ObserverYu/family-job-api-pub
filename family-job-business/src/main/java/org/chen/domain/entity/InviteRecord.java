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
 * 
 * </p>
 *
 * @author YuChen
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("invite_record")
public class InviteRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邀请人id
     */
    private Long inviterId;

    private Long userId;

    private String inviterAvatar;

    private String userAvatar;

    /**
     * 0-待接收 1-已接收 2-已拒绝 3-已退出
     */
    private Integer state;

    /**
     * 使用的邀请码
     */
    private String inviteCode;

    /**
     * 家庭id
     */
    private Long familyId;

    /**
     * 审核时间
     */
    private Date checkTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 退出时间
     */
    private Date quitTime;

    private String userName;

    private String inviterName;

    private String familyName;
    private String familyCreatorName;
    private String familyCreatorAvatar;
    private String userType;


}

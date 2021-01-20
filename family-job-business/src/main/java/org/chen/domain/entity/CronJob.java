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
 * 定时任务
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cron_job")
public class CronJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0-每周,1-每日,2-每月
     */
    private Integer type;

    /**
     * 状态 0-未启用 1-启用
     */
    private Integer state;

    /**
     * 任务id
     */
    private Long jobId;

    /**
     * 任务名字
     */
    private String name;

    /**
     * 任务类型 0-其他
     */
    private Long typeId;

    /**
     * 家庭id
     */
    private Long familyId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 监督人id
     */
    private Long watchdogId;

    /**
     * 监督人姓名
     */
    private String watchdogName;

    /**
     * 监督人头像
     */
    private String watchdogAvatar;

    /**
     * 每次积分
     */
    private Long points;

    /**
     * 次数
     */
    private Integer times;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    private String remark;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 创建者头像
     */
    private String creatorAvatar;

    /**
     * 创建者头像
     */
    private String creatorName;
    private Long version;


    private Integer  canStep;
    private Long  costStep;



}

package org.chen.domain.result;

import lombok.Data;
import lombok.ToString;

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
@ToString
public class CronJobResult {

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
    private Integer typeId;

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

    private String cronTypeStr;

    private String stateStr;



}

package org.chen.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 券信息
 * </p>
 *
 * @author YuChen
 * @since 2020-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("job_user")
@ToString
public class JobUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号  付钱用
     */
    private String jobNo;

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
     * 过期时间
     */
    private Date expiredTime;

    /**
     * 状态 0-待同意 1-待完成 2-已过期 3-已拒绝 4-已完成 5-付钱完成
     */
    private Integer state;

    /**
     * 任务创建人id
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

    /**
     * 用户头像
     */
    private String userAvatar;



    /**
     * 用户名字
     */
    private String userName;

    /**
     * 任务id
     */
    private Long jobId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 买过价格
     */
    private BigDecimal cost;

    /**
     * 是否能付钱解决 0-不行 1-可以
     */
    private Integer canMoney;

    /**
     * 处理时间
     */
    private Date checkTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 拒绝理由
     */
    private String refuseReason;

    private Integer  canStep;
    private Long  costStep;

    private Integer  stepFinish;

    private Long version;
    private Integer createType;

    private Long points;
    private Long cronJobId;
    private Integer cronType;
    private Long stepId;






}

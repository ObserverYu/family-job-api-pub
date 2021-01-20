package org.chen.domain.result;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class JobUserResult {
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
     * 任务创建人id
     */
    private Long creatorId;

    /**
     * 过期时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date expiredTime;

    /**
     * 状态 0-待同意 1-待完成 2-已过期 3-已拒绝 4-已完成 5-付钱完成
     */
    private Integer state;

    /**
     * 创建者头像
     */
    private String creatorAvatar;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 创建者头像
     */
    private String creatorName;

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
    private Integer  stepFinish;
    private Long  costStep;

    private String stateStr;
    private Long version;

    private Integer createType;
    private Long points;
    private Long cronJobId;

    private Integer cronType;

    private String cronTypeStr;

    private String createTypeStr;

    private String allTypeStr;

    private Long stepId;


}

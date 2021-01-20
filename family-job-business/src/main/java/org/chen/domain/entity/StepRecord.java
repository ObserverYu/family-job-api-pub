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
 * 步数记录
 * </p>
 *
 * @author YuChen
 * @since 2020-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("step_record")
public class StepRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 总步数
     */
    private Long allStep;

    /**
     * 日期
     */
    private String startDate;

    /**
     * 剩余总步数
     */
    private Long remainingStep;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号   乐观锁专用
     */
    private Long version;

    private Long userId;

    /**
     * 微信自带的时间戳
     */
    private Long timestamp;


}

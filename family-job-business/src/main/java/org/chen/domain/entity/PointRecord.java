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
 * 点数记录
 * </p>
 *
 * @author YuChen
 * @since 2020-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("point_record")
public class PointRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0-指派支出 1-指派收入 2-额外任务收入 3-定时任务收入
     */
    private Integer type;

    /**
     * 额度
     */
    private Long points;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 家庭id
     */
    private Long familyId;
    private Long jobUserId;

    /**
     * 创建时间
     */
    private Date createTime;


}

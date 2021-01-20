package org.chen.domain.result;

import lombok.Data;

import java.util.Date;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class CustomizedJobTypeResult {

    private Long id;

    private String name;

    /**
     * 创建时间
     */
    private Date updateTime;

    /**
     * 修改时间
     */
    private Date createTime;

    private Integer sort;

    private Integer deleted;

    private Integer count;




}

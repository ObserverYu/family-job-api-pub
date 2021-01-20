package org.chen.domain.result;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class StatisticsResult {

    /**
     * 项目名
     */
    private String name;

    /**
     * 值
     */
    private Long value;


    /**
     * 微信名字
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * id
     */
    private Long id;


}

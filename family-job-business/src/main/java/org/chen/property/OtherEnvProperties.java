package org.chen.property;

import lombok.Data;
import lombok.ToString;

/**
 * 测试自定义路由
 *
 * @author YuChen
 * @date 2020/5/18 15:29
 **/

@ToString
@Data
public class OtherEnvProperties /*extends RouterBaseConfigBean*/{

    private String key;

    private String value;

}

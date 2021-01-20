package org.chen.framework.result;

import lombok.Data;

/**
 * 返回模型类
 *
 * @author YuChen
 * @date 2018/12/6
 **/
@Data
public class ResultModel<T> {

	private int code;

	private String message;

	private T data;

	private long timestamp;

	private long runTime;

}

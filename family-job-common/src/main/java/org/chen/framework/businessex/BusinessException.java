package org.chen.framework.businessex;

import lombok.Data;

/**
 * 业务异常
 *  
 * @author YuChen
 * @date 2020/2/14 12:05
 **/

@Data
public class BusinessException extends RuntimeException{

	private Integer code;

	private String message;

	public BusinessException(BusinessExceptionEnum businessExceptionEnum){
		this.code = businessExceptionEnum.getCode();
		this.message = businessExceptionEnum.getMessage();
	}

	public BusinessException(String msg, Integer code){
		this.message = msg;
		this.code = code;
	}
}

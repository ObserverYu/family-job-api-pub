package org.chen.framework.businessex;

/**
 * 业务异常
 *
 * @author YuChen
 * @date 2020/2/14 12:05
 **/

public enum BusinessExceptionEnum {

	SERVER_ERROR("服务繁忙,请稍后再试",500)

	,TOKEN_ERROR("token不存在或已过期",401)

	,REQUEST_NO_USERID("找不到userId",700)

	,ROW_IS_LOCKING("网络拥堵,请重试",501)

	,REPEAT_REQUEST("重复提交,请稍等",301)

	,NO_AUTH("无权操作",407)

	,STATE_EX("状态错误",408)

	,TOKEN_EMPTY("头部未找到auth-token",700)
	;

	private Integer code;

	private String message;

	BusinessExceptionEnum(String msg, Integer code){
		this.code = code;
		this.message = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

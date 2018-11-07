package com.joker.lbs.util;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @ClassName: RestResultDto
 * @Description: 前端统一格式返回体
 * @author XS guo
 * @date 2017年7月24日 下午3:04:58
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResultDto<T> implements Serializable {
	/**
	 * 返回结果成功为0
	 */
	public static final Integer RESULT_SUCC = 0;
	/**
	 * 返回结果失败为1
	 */
	public static final Integer RESULT_FAIL = 1;

	/**
	 * 默认返回
	 */
	private Integer result = RESULT_SUCC;

	/**
	 * 错误吗
	 */
	private String errCode = StringUtils.EMPTY;

	/**
	 * 附属信息
	 */
	private String msg = StringUtils.EMPTY;

	/**
	 * 异常信息
	 */
	private String exception = StringUtils.EMPTY;

	/**
	 * 实际返回数据
	 */
	private T data;

	public static <T> RestResultDto<T> newFalid(String exception) {
		return newResult(RESULT_FAIL, "", null, exception);
	}

	public static <T> RestResultDto<T> newFalid(String msg, String exception) {
		return newResult(RESULT_FAIL, msg, null, exception);
	}

	public static <T> RestResultDto<T> newSuccess() {
		return newResult(RESULT_SUCC, "", null, "");
	}

	public static <T> RestResultDto<T> newSuccess(T data) {
		return newResult(RESULT_SUCC, "", data, "");
	}

	public static <T> RestResultDto<T> newSuccess(T data, String msg) {
		return newResult(RESULT_SUCC, msg, data, "");
	}

	private static <T> RestResultDto<T> newResult(Integer result, String msg, T data, String exception) {
		return new RestResultDto<T>(result, msg, data, exception);
	}

	public RestResultDto() {

	}

	public RestResultDto(Integer result, String msg, T data, String exception) {
		this.result = result;
		this.msg = msg;
		this.data = data;
		this.exception = exception;
	}

	public RestResultDto(Integer result, String msg, T data, String exception, String errCode) {
		this.result = result;
		this.msg = msg;
		this.data = data;
		this.exception = exception;
		this.errCode = errCode;
	}

	public RestResultDto(Integer result, String msg, T data) {
		this.result = result;
		this.msg = msg;
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

}

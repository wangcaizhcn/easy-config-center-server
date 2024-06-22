package com.github.wangcaizhcn.config.basic;

import java.io.Serializable;

/**
 * Restful 接口统一返回数据的格式
 * @author wangcai
 */
public class ApiResult<T> implements Serializable {
	
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private String code;

    /**
     * 信息
     */
    private String message;

    /**
     * 结果
     */
    private T data;

    /**
     * 总记录数，在返回列表数据时生效
     */
    private long total;
    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
    
}

package com.github.wangcaizhcn.config.basic;

public class SeasException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    
    /**
     * 业务状态码
     */
    private String code;
    
    /**
     * 原始异常
     */
    private Exception cause;
    
    /**
     * 信息
     */
    private String message;

    /**
     * 构造空异常
     */
    public SeasException() {
    	super();
    }
    
    /**
     * 根据原始异常构造信息异常
     * @param cause
     */
    public SeasException(Exception cause) {
    	super(cause);
    	this.cause = cause;
    }
    
    /**
     * 根据异常码构造异常
     * @param code
     */
    public SeasException(String code) {
    	super();
    	this.code = code;
    }
    
    /**
     * 根据原始异常和异常码构造异常
     * @param cause
     * @param code
     */
    public SeasException(Exception cause, String code) {
    	super(cause);
    	this.cause = cause;
    	this.code = code;
    }
    
    /**
     * 根据异常吗，异常信息构造异常
     * @param code
     * @param message
     */
    public SeasException(String code, String message) {
    	super();
    	this.code = code;
    	this.message = message;
    }
    
    /**
     * 根据原始异常、异常码、异常信息构造异常
     * @param cause
     * @param code
     * @param message
     */
    public SeasException(Exception cause, String code, String message) {
    	super(cause);
    	this.code = code;
    	this.cause = cause;
    	this.message = message;
    }

	public String getCode() {
		return code;
	}

	public synchronized Exception getCause() {
		return cause;
	}

	public String getMessage() {
		return message;
	}
}

package com.github.wangcaizhcn.config.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理，只处理com.github.wangcaizhcn.config包下的，不影响其他业务包
 */
@ControllerAdvice(basePackages = {"com.github.wangcaizhcn.config"})
public class SeasExceptionHandler {

	private final static Logger log = LoggerFactory.getLogger(SeasExceptionHandler.class); 
	
	/**
	 * 业务异常
	 * @param <T>
	 * @param ex
	 * @return
	 */
    @ExceptionHandler(SeasException.class)
    @ResponseBody
    public <T> ApiResult<T> exceptionHandler(SeasException ex) {
        log.error("<== 异常：({}, {})", ex.getCode(), ex.getMessage());
        return ApiResultTemplate.failure(ex);
    }

    /**
     * Assert 拦截
     * @param <T>
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public <T> ApiResult<T> exceptionHandler(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return ApiResultTemplate.failure(CommonResultCodeConstant.ARGUMENT_INVALID_ERROR, ex.getMessage());
    }

    /**
     * 未预料异常
     * @param <T>
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T> ApiResult<T> exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ApiResultTemplate.failure(CommonResultCodeConstant.SYSTEM_ERROR, ex.getMessage());
    }
}

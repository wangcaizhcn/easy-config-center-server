package com.github.wangcaizhcn.config.basic;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ApiResultTemplate {

    private static <T> ApiResult<T> result(T data, String code, String message) {
        
    	ApiResult<T> result = new ApiResult<>();
    	result.setCode(code);
    	result.setMessage(message);
    	result.setData(data);
    	
    	return result;
    	
    }

    public static <T> ApiResult<T> result(String code) {
        return result(null, code, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return result(data, CommonResultCodeConstant.REQUEST_SUCCESS, null);
    }

    public static <T> ApiResult<T> success(T data, String message) {
        return result(data, CommonResultCodeConstant.REQUEST_SUCCESS, message);
    }

    public static <T> ApiResult<T> success(String message) {
        return result(null, CommonResultCodeConstant.REQUEST_SUCCESS, message);
    }

    public static <T> ApiResult<List<T>> success(List<T> list) {
    	
    	ApiResult<List<T>> result = new ApiResult<>();
    	
    	result.setCode(CommonResultCodeConstant.REQUEST_SUCCESS);
    	result.setData(list);
    	result.setTotal(list.size());
    	
    	return result;
    }
    
    public static <T> ApiResult<List<T>> success(List<T> list, String message) {
    	
    	ApiResult<List<T>> result = new ApiResult<>();
    	
    	result.setCode(CommonResultCodeConstant.REQUEST_SUCCESS);
    	result.setData(list);
    	result.setTotal(list.size());
    	result.setMessage(message);
    	
    	return result;
    }
    
    public static <T> ApiResult<List<T>> success(PageableResult<T> page) {
    	
    	ApiResult<List<T>> result = new ApiResult<>();
    	
    	result.setCode(CommonResultCodeConstant.REQUEST_SUCCESS);
    	result.setData(page.getData());
    	result.setTotal(page.getTotal());
    	
    	return result;
    }

    public static <T> ApiResult<T> success() {
        return result(CommonResultCodeConstant.REQUEST_SUCCESS);
    }

    public static <T> ApiResult<T> failure() {
        return result(CommonResultCodeConstant.SYSTEM_ERROR);
    }

    public static <T> ApiResult<T> failure(String code) {
        return failure(code, null);
    }

    public static <T> ApiResult<T> failure(String code, String message) {
        if (CommonResultCodeConstant.REQUEST_SUCCESS.equals(code) ) {
            throw new RuntimeException("成功的标识码不能用于失败返回");
        }
        return result(null, code, message);
    }

    public static <T> ApiResult<T> failure(SeasException ex) {
    	ApiResult<T> result = new ApiResult<>();
    	result.setCode(StringUtils.isBlank(ex.getCode()) ? CommonResultCodeConstant.SYSTEM_ERROR : ex.getCode());
    	result.setMessage(ex.getMessage());
        return result;
    }

}

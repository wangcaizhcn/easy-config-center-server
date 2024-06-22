package com.github.wangcaizhcn.config.basic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

	private final static Logger log = LoggerFactory.getLogger(ControllerAspect.class);
	
	@Pointcut("execution(* com.github.wangcaizhcn.config..*Controller*.*(..))")
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    	Class<?> clazz = joinPoint.getTarget().getClass();
        long startTime = System.currentTimeMillis();
        Object obj = joinPoint.proceed();
        log.info("调用方法{}.{}, 耗时{}毫秒", clazz.getName(), joinPoint.getSignature().getName(), (System.currentTimeMillis() - startTime));
        return obj;
    }
    
}

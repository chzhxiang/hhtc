package com.jadyer.seed.comm.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.IPUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {
    @Around("execution(public * com.jadyer..*Controller.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Object respData;
        long startTime = System.currentTimeMillis();
        String methodInfo = joinPoint.getTarget().getClass().getSimpleName() + "." + joinPoint.getSignature().getName();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(null == attributes){
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        if(request.getServletPath().startsWith("/wx/orderinout")){
            return joinPoint.proceed();
        }

        LogUtil.getLogger().info("{}()-->{}被调用，客户端IP={}，入参为[{}]", methodInfo, request.getRequestURI(), IPUtil.getClientIP(request), JadyerUtil.buildStringFromMap(request.getParameterMap()));
        respData = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        String returnInfo;
        if(null!=respData && respData.getClass().isAssignableFrom(ResponseEntity.class)){
            returnInfo = "ResponseEntity";
        }else{
            returnInfo = JSON.toJSONStringWithDateFormat(respData, JSON.DEFFAULT_DATE_FORMAT, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
        }
        LogUtil.getLogger().info("{}()-->{}被调用，出参为[{}]，Duration[{}]ms",methodInfo,request.getRequestURI(),returnInfo,endTime-startTime);
        LogUtil.getLogger().info("---------------------------------------------------------------------------------------------");
        return respData;
    }
}
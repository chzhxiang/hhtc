package com.jadyer.seed.comm.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取IP的工具类
 * ----------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->增加获取服务端IP的方法
 * @history v1.0-->增加获取客户端IP的方法
 * ----------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/4/14 20:55.
 */
public final class IPUtil {
    private IPUtil(){}

    public static String getClientIP(HttpServletRequest request){
        String IP = request.getHeader("x-forwarded-for");
        if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("Proxy-Client-IP");
        }
        if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
            IP = request.getHeader("WL-Proxy-Client-IP");
        }
        if(null==IP || 0==IP.length() || "unknown".equalsIgnoreCase(IP)){
            IP = request.getRemoteAddr();
        }
        if(null!=IP && IP.length()>15){
            if(IP.indexOf(",") > 0){
                IP = IP.substring(0, IP.indexOf(","));
            }
        }
        return IP;
    }
}
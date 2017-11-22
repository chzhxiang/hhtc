package com.jadyer.seed.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工具类
 * @version v2.3
 * @history v2.3-->修复获取logger方法中，可能会获取到其它线程绑定的logger，导致获取到的不是想要的logger
 * @history v2.2-->优化Log获取为显式指定所要获取的Log,未指定时默认取上一次的Log,没有上一次的则取defaultLog
 * @history v2.1-->新增多线程情景下的日志集中打印功能
 * @history v2.0-->新增日志的数据库保存和邮件发送功能
 * @history v1.0-->通过<code>java.lang.ThreadLocal</code>实现日志记录器
 * Created by 玄玉<http://jadyer.cn/> on 2012/11/18 18:19.
 */
public final class LogUtil {
    private static final String LOGGER_NAME_DEFAULT = "defaultLogger";
    private static final String LOGGER_NAME_QUARTZ = "quartzLogger";
    private static final String LOGGER_NAME_TASK = "taskLogger";
    private static final String LOGGER_NAME_MQ = "mqLogger";
    private LogUtil(){}
    private static ThreadLocal<Logger> currentLoggerMap = new ThreadLocal<>();
    private static Logger defaultLogger = LoggerFactory.getLogger(LOGGER_NAME_DEFAULT);
    private static Logger quartzLogger = LoggerFactory.getLogger(LOGGER_NAME_QUARTZ);
    private static Logger taskLogger = LoggerFactory.getLogger(LOGGER_NAME_TASK);
    private static Logger mqLogger = LoggerFactory.getLogger(LOGGER_NAME_MQ);

    public static Logger getLogger() {
        Logger logger = currentLoggerMap.get();
        if(null == logger){
            return defaultLogger;
        }else{
            return logger;
        }
    }


    public static Logger getQuartzLogger() {
        Logger logger = currentLoggerMap.get();
        if(null!=logger && LOGGER_NAME_QUARTZ.equals(logger.getName())){
            return logger;
        }
        currentLoggerMap.set(quartzLogger);
        return quartzLogger;
    }


    public static Logger getMQLogger() {
        Logger logger = currentLoggerMap.get();
        if(null!=logger && LOGGER_NAME_MQ.equals(logger.getName())){
            return logger;
        }
        currentLoggerMap.set(mqLogger);
        return mqLogger;
    }


    public static Logger logToTask() {
        return taskLogger;
    }


    private static ConcurrentHashMap<String, String> suspendMap = new ConcurrentHashMap<>();
    public static void setSuspend(String suspend){
        suspendMap.put("suspend", suspend);
    }
    public static String getSuspend(){
        return suspendMap.get("suspend");
    }
}
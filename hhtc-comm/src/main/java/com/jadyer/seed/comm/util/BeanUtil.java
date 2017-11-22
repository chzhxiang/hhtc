package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean工具类
 * @version v1.3
 * @history v1.3-->mapTobean()方法增加除String外的int和long类型支持
 * @history v1.2-->增加mapTobean()方法
 * @history v1.1-->增加beanToMap()方法
 * @history v1.0-->初建
 * Created by 玄玉<http://jadyer.cn/> on 2017/5/18 17:22.
 */
public final class BeanUtil {
    private BeanUtil(){}

    public static <T> T requestToBean(HttpServletRequest request, Class<T> beanClass){
        try{
            T bean = beanClass.newInstance();
            for(Field field : beanClass.getDeclaredFields()){
                String methodName = "set" + StringUtils.capitalize(field.getName());
                try{
                    String fieldValue = request.getParameter(field.getName());
                    fieldValue = null==fieldValue ? "" : fieldValue;
                    beanClass.getMethod(methodName, String.class).invoke(bean, URLDecoder.decode(fieldValue, "UTF-8"));
                }catch(Exception e){
                    //ignore exception
                }
            }
            return bean;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    public static <T, E> void copyProperties(T source, E target){
        Method[] sourceMethods = source.getClass().getDeclaredMethods();
        Method[] targetMethods = target.getClass().getDeclaredMethods();
        for(Method sourceMethod : sourceMethods){
            if(sourceMethod.getName().startsWith("get") || sourceMethod.getName().startsWith("is")){
                String sourceFieldName;
                if(sourceMethod.getName().startsWith("get")){
                    sourceFieldName = "set" + sourceMethod.getName().substring(3);
                }else{
                    sourceFieldName = "set" + sourceMethod.getName().substring(2);
                }
                for(Method targetMethod : targetMethods){
                    if(targetMethod.getName().equals(sourceFieldName)){
                        if(sourceMethod.getReturnType().isAssignableFrom(targetMethod.getParameterTypes()[0])){
                            try {
                                targetMethod.invoke(target, sourceMethod.invoke(source));
                                break;
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("属性拷贝失败", e);
                            }
                        }
                    }
                }
            }
        }
    }


    public static Map<String, String> beanToMap(Object bean) {
        if(null == bean){
            return new HashMap<>();
        }
        try {
            Map<String, String> dataMap = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor obj : propertyDescriptors){
                String propertyName = obj.getName();
                if(!"class".equals(propertyName)){
                    Object result = obj.getReadMethod().invoke(bean);
                    if(null != result){
                        dataMap.put(propertyName, result.toString());
                    }
                }
            }
            return dataMap;
        }catch(Exception e){
            throw new RuntimeException("beanToMap发生异常，堆栈轨迹如下", e);
        }
    }


    /**
     * Bean的属性值目前只支持String、int、long，其它未做兼容
     */
    public static <T> T mapTobean(Map<String, String> dataMap, Class<T> beanClass) {
        try{
            T bean = beanClass.newInstance();
            if(null==dataMap || dataMap.isEmpty()){
                return bean;
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor obj : propertyDescriptors){
                String propertyName = obj.getName();
                if(dataMap.containsKey(propertyName)){
                    switch(obj.getPropertyType().getName()){
                        case "int"  : obj.getWriteMethod().invoke(bean, Integer.parseInt(dataMap.get(propertyName))); break;
                        case "long" : obj.getWriteMethod().invoke(bean, Long.parseLong(dataMap.get(propertyName)));   break;
                        default: obj.getWriteMethod().invoke(bean, dataMap.get(propertyName));
                    }
                }
            }
            return bean;
        }catch(Exception e){
            throw new RuntimeException("mapTobean发生异常，堆栈轨迹如下", e);
        }
    }
}
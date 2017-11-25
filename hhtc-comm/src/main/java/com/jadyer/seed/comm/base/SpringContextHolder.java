package com.jadyer.seed.comm.base;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext持有器
 * Created by 玄玉<http://jadyer.cn/> on 2015/2/27 10:01.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext applicationContext;

    public static Object getBean(String beanName){
        assertContextInjected();
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType){
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(null != SpringContextHolder.applicationContext){
            LogUtil.getLogger().info("SpringContextHolder中的ApplicationContext将被覆盖，原ApplicationContext为：{}" ,SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    private static void clearHolder(){
        LogUtil.getLogger().warn("清除SpringContextHolder中的ApplicationContext：{}", applicationContext);
        applicationContext = null;
    }

    private static void assertContextInjected(){
        Validate.validState(null!=applicationContext, "ApplicaitonContext属性未注入，请定义SpringContextHolder...");
    }
}
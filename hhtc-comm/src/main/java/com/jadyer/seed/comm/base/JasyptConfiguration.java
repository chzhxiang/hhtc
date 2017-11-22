package com.jadyer.seed.comm.base;

import com.jadyer.seed.comm.util.LogUtil;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

@Configuration
@ConditionalOnClass(StringEncryptor.class)
@PropertySource(value={"${jasypt.file:classpath:config/encrypted.properties}"})
public class JasyptConfiguration implements EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private String getProperty(Environment environment, String key, String defaultValue){
        if(null == environment.getProperty(key)){
            LogUtil.getLogger().info("Encryptor config not found for property {}, using default value: {}", key, defaultValue);
        }
        return environment.getProperty(key, defaultValue);
    }


    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public BeanFactoryPostProcessor propertySourcesPostProcessor(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(this.getProperty(this.environment, "jasypt.encryptor.password", "http://jadyer.cn/"));
        config.setAlgorithm(this.getProperty(this.environment, "jasypt.encryptor.algorithm", "PBEWithMD5AndDES"));
        config.setKeyObtentionIterations(this.getProperty(this.environment, "jasypt.encryptor.keyObtentionIterations", "1000"));
        config.setPoolSize(this.getProperty(this.environment, "jasypt.encryptor.poolSize", "1"));
        config.setProviderName(this.getProperty(this.environment, "jasypt.encryptor.providerName", "SunJCE"));
        config.setSaltGeneratorClassName(this.getProperty(this.environment, "jasypt.encryptor.saltGeneratorClassname", "org.jasypt.salt.RandomSaltGenerator"));
        config.setStringOutputType(this.getProperty(this.environment, "jasypt.encryptor.stringOutputType", "hexadecimal"));
        encryptor.setConfig(config);
        return new EnableEncryptablePropertySourcesPostProcessor(encryptor);
    }


    private class EnableEncryptablePropertySourcesPostProcessor implements BeanFactoryPostProcessor {
        private StringEncryptor encryptor;
        EnableEncryptablePropertySourcesPostProcessor(StringEncryptor encryptor){
            this.encryptor = encryptor;
        }
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            MutablePropertySources propertySources = ((ConfigurableEnvironment)environment).getPropertySources();
            for(org.springframework.core.env.PropertySource<?> obj : propertySources){
                if(obj instanceof ResourcePropertySource){
                    propertySources.replace(obj.getName(), new PropertySourceWrapper((ResourcePropertySource)obj));
                }
            }
        }
        private class PropertySourceWrapper extends MapPropertySource {
            PropertySourceWrapper(ResourcePropertySource propertySource) {
                super(propertySource.getName(), propertySource.getSource());
            }
            @Override
            public Object getProperty(String name) {
                Object value = super.getProperty(name);
                if(value instanceof String){
                    String stringValue = String.valueOf(value);
                    if(PropertyValueEncryptionUtils.isEncryptedValue(stringValue)){
                        value = PropertyValueEncryptionUtils.decrypt(stringValue, encryptor);
                    }
                }
                return value;
            }
        }
    }
}
package com.onevour.core.applications.rest.configuration;


import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Configuration
public class RestBeanRegistry extends BasicRestBuilder implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (Objects.isNull(applicationContext)) {
            log.info("ApplicationContext still null");
            return;
        }
        extractedFromContext(beanFactory);

        // issue on unit test

//        Set<Class<?>> classes = getAllInterfacesInProject(RestRepository.class);
//        log.info("RestRepository found {}", classes.size());
//        for (Class<?> c : classes) {
//            log.trace("RestRepository class {}", c);
//            Object o = createProxyBean(c, beanFactory);
//            beanFactory.registerSingleton(variableName(c.getSimpleName()), o);
//            beanFactory.autowireBean(o);
//        }
    }

    private void extractedFromContext(ConfigurableListableBeanFactory beanFactory) {
        int size = 0;
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
        for (String beanName : beanNames) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            BeanDefinition beanDefinition = configurableApplicationContext.getBeanFactory().getBeanDefinition(beanName);
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(className);
                String packageName = beanClass.getPackage().getName();
                Set<Class<?>> classes = getAllInterfacesInPackage(RestRepository.class, packageName);
                size += classes.size();
                for (Class<?> c : classes) {
                    Object o = createProxyBean(c, beanFactory);
                    beanFactory.registerSingleton(variableName(c.getSimpleName()), o);
                    beanFactory.autowireBean(o);
                }
                log.trace("package scan {}", packageName);
            } catch (ClassNotFoundException ignore) {

            }
        }
        log.info("RestRepository found {}", size);
    }
}
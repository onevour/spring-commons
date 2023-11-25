package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
public class RestBeanRegistry extends BasicRestBuilder implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Set<Class<?>> classes = getAllInterfaces(RestRepository.class);
        for (Class<?> c : classes) {
            Object o = createProxyBean(c, beanFactory);
            beanFactory.registerSingleton(variableName(c.getSimpleName()), o);
            beanFactory.autowireBean(o);
        }
    }

}
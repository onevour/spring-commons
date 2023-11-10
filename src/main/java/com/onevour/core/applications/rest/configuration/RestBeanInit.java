package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RestBeanInit implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Set<Class<?>> classes = getAllInterfaces(RestRepository.class);
        for (Class<?> c : classes) {
            log.info("name simple {}", variableName(c.getSimpleName()));
            Object o = createProxyBean(c);
            beanFactory.registerSingleton(variableName(c.getSimpleName()), o);
            beanFactory.autowireBean(o);
        }
    }

    private String variableName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    private Set<Class<?>> getAllInterfaces(Class<?> parentInterface) {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        Set<Class<?>> interfaces = new HashSet<>();
        try {
            for (Resource resource : new PathMatchingResourcePatternResolver().getResources("classpath*:/**/*.class")) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                Class<?> currentClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                if (currentClass.equals(parentInterface)) continue;
                if (currentClass.isInterface() && parentInterface.isAssignableFrom(currentClass)) {
                    interfaces.add(currentClass);
                }
            }
        } catch (NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError | Exception e) {
            // do nothing
        }
        return interfaces;
    }

    private Object createProxyBean(Class<?> beanClass) {
        ProxyFactory proxyFactory = new ProxyFactory();
        // use scanner spring
        proxyFactory.addInterface(beanClass);
        // advisor
        proxyFactory.addAdvice(new RestExecutorMethodHandler());
        return proxyFactory.getProxy();
    }

}
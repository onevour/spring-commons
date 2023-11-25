package com.onevour.core.applications.rest.configuration;

import com.asliri.core.applications.rest.handler.RestExecutorMethodHandler;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.util.HashSet;
import java.util.Set;

public class BasicRestBuilder {

    protected String variableName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    protected Set<Class<?>> getAllInterfaces(Class<?> parentInterface) {
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
    // Access the property value

    protected Object createProxyBean(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory) {
        ProxyFactory proxyFactory = new ProxyFactory();
        // use scanner spring
        proxyFactory.addInterface(beanClass);
        // advisor
        proxyFactory.addAdvice(new RestExecutorMethodHandler(beanFactory));
        return proxyFactory.getProxy();
    }
}

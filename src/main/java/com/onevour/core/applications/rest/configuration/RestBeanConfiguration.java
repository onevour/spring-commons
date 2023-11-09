package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
public class RestBeanConfiguration implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    public String convertFirstToLower(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        scanClass();
        Set<Class<?>> classes = getAllInterfaces(RestRepository.class);
        for (Class<?> c: classes){
            log.info("name simple {}", convertFirstToLower(c.getSimpleName()));
            Object o = createProxyBean(c);
            beanFactory.registerSingleton("authRepository", o);
            beanFactory.autowireBean(o);
        }
//        Object o = createProxyBean(AuthRepository.class);
//        beanFactory.registerSingleton("authRepository", o);
//        beanFactory.autowireBean(o);
    }

    private void scanClass() {
//        try {
//            String packagePath = "com.onevour.core.applications.rest.services";
        String packagePath = "";
        //Set<Class<?>> classes = getAllInterfacesInPackage(packagePath, RestRepository.class);
        Set<Class<?>> classes = getAllInterfaces(RestRepository.class);
        log.info("found scan class {}", classes.size());
        for (Class<?> c : classes) {
            log.info("found {}", c);
        }
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

    }

    public Set<Class<?>> getAllInterfaces(Class<?> parentInterface) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        Set<Class<?>> interfaces = new HashSet<>();
        try {
            for (Resource resource : new PathMatchingResourcePatternResolver().getResources("classpath*:/**/*.class")) {
//            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                Class<?> currentClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                if (currentClass.equals(parentInterface)) continue;
                if (currentClass.isInterface() && parentInterface.isAssignableFrom(currentClass)) {
                    interfaces.add(currentClass);
                }
//                Class<?> currentClass = Class.forName(metadataReader.getClassMetadata().getClassName());

//                if (currentClass.isInterface()) {
//                    interfaces.add(currentClass);
//                }
//                }


            }
        } catch (NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError | Exception e) {
//                log.error("error {}", e.getMessage());
        }

        return interfaces;
    }

    public Set<Class<?>> getAllInterfacesInPackage(String basePackage, Class<?> parentInterface) throws IOException, ClassNotFoundException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        Set<Class<?>> interfaces = new HashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
//        String packageSearchPath = new PathMatchingResourcePatternResolver().getResources("classpath*:/**/*.class");
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            Class<?> currentClass = Class.forName(metadataReader.getClassMetadata().getClassName());
            if (currentClass.isInterface() && parentInterface.isAssignableFrom(currentClass)) {
                interfaces.add(currentClass);
            }
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
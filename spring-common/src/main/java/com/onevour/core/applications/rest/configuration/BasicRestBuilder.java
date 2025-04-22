package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class BasicRestBuilder {

    // bugs ketika unit test
    protected Set<Class<?>> getAllInterfacesInProject(Class<?> parentInterface) {
        return getAllInterfacesInPackage(parentInterface, groupNameMainApplication().toArray(new String[0]));
    }

    protected Set<Class<?>> getAllInterfacesInPackage(Class<?> parentInterface, String... basePackageName) {
        Set<Class<?>> interfaces = new HashSet<>();
        Set<String> values = new HashSet<>(Arrays.asList(basePackageName));
        log.info("scan found {} packages", values.size());
        for (String basePackage : values) {
            SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

            Resource[] resources = new Resource[0];
            try {
                String packagePath = basePackage.replace('.', '/');
                String pattern = "classpath*:" + packagePath + "/**/*.class";
                // scan with pattern
                log.info("scan with pattern {}", pattern);
                resources = new PathMatchingResourcePatternResolver().getResources(pattern);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            log.trace("found resource {}", resources.length);
            for (Resource resource : resources) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String packageName = metadataReader.getClassMetadata().getClassName();
                        log.trace("scan package resource {}", packageName);
                        Class<?> currentClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                        if (currentClass.isInterface() && parentInterface.isAssignableFrom(currentClass)) {
                            interfaces.add(currentClass);
                        }
                    } catch (IOException | ClassNotFoundException | LinkageError e) {
                        // ignore
                    }
                });
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        return interfaces;
    }

    protected Object createProxyBean(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory) {
        ProxyFactory proxyFactory = new ProxyFactory();
        // use scanner spring
        proxyFactory.addInterface(beanClass);
        // advisor
        proxyFactory.addAdvice(new RestExecutorMethodHandler(beanFactory));
        return proxyFactory.getProxy();
    }

    protected String variableName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    protected Set<String> groupNameMainApplication() {

        Set<String> excludes = new HashSet<>();
        excludes.add("com.onevour.core");
        excludes.add("com.sun");
        excludes.add("java.util");
        excludes.add("java.lang");
        excludes.add("org.springframework");
        excludes.add("jdk.internal");
        excludes.add("worker.org.gradle");
        excludes.add("org.junit");

        Set<String> values = new HashSet<>();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            try {
                Class<?> cls = Class.forName(element.getClassName());
                Package packageTmp = cls.getPackage();
                log.info("start scan {}", packageTmp);
                if (Objects.isNull(packageTmp)) continue;
                String packageName = packageTmp.getName();

                if (excludes.stream().anyMatch(o -> {
                    boolean res = packageName.startsWith(o);
                    log.trace("{} start with {} result {}", o, packageName, res);
                    return res;
                })) {
                    log.trace("skip package {}", packageName);
                    continue;
                }
                if (hasMainMethod(cls)) {
                    values.add(packageName);
                    log.info("package scan {} has main", packageTmp);
                    continue;
                }
                if (isSpringBootMain(cls)) {
                    values.add(packageName);
                    log.info("package scan {} is SpringBootMain", packageTmp);
                    continue;
                }
                log.info("package scan {} main not found", packageTmp);
            } catch (ClassNotFoundException e) {
                // Abaikan error dan lanjutkan iterasi
            }
        }
        return values;
    }

    private boolean hasMainMethod(Class<?> cls) {
        try {
            cls.getDeclaredMethod("main", String[].class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private boolean isSpringBootMain(Class<?> cls) {
        return cls.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class);
    }

}

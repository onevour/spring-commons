package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class BasicRestBuilder {

    /**
     * Cari semua interface turunan {@code parentInterface} di dalam {@code basePackages}
     * dengan membaca bytecode lewat ASM ({@link MetadataReader}), <b>tanpa memuat class</b>.
     * Hasilnya hanya nama class (String), seperti yang dilakukan Spring Data.
     * <p>
     * Resource dan metadata dibaca lewat {@code classLoader} yang diberikan (classloader
     * context), bukan classloader library, supaya tetap bekerja saat Spring Boot Devtools
     * memuat class aplikasi lewat RestartClassLoader.
     */
    protected Set<String> scanInterfaceNames(Class<?> parentInterface, ClassLoader classLoader, Collection<String> basePackages) {
        Set<String> names = new LinkedHashSet<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        AssignableTypeFilter filter = new AssignableTypeFilter(parentInterface);

        Set<String> packages = new LinkedHashSet<>(basePackages);
        log.trace("scan {} packages for {}", packages.size(), parentInterface.getName());
        for (String basePackage : packages) {
            String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
            Resource[] resources;
            try {
                resources = resolver.getResources(pattern);
            } catch (IOException e) {
                log.warn("cannot scan package {} with pattern {}: {}", basePackage, pattern, e.getMessage());
                continue;
            }
            log.trace("scan {} found {} resources", pattern, resources.length);
            for (Resource resource : resources) {
                if (!resource.isReadable()) continue;
                try {
                    MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata metadata = reader.getClassMetadata();
                    if (!metadata.isInterface()) continue;
                    if (parentInterface.getName().equals(metadata.getClassName())) continue;
                    if (filter.match(reader, metadataReaderFactory)) {
                        log.trace("found {}", metadata.getClassName());
                        names.add(metadata.getClassName());
                    }
                } catch (IOException e) {
                    // jangan ditelan diam-diam: masalah classpath harus langsung terlihat
                    log.warn("cannot read class metadata from {}: {}", resource, e.getMessage());
                }
            }
        }
        return names;
    }

    /**
     * @deprecated memuat class lewat classloader pemanggil (library), sehingga interface
     * aplikasi tidak ditemukan saat Devtools aktif. Pakai {@link #scanInterfaceNames} dan
     * biarkan {@link RestRepositoryFactoryBean} memuat class lewat classloader context.
     */
    @Deprecated
    protected Set<Class<?>> getAllInterfacesInPackage(Class<?> parentInterface, String... basePackageName) {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        for (String name : scanInterfaceNames(parentInterface, classLoader, Arrays.asList(basePackageName))) {
            try {
                interfaces.add(ClassUtils.forName(name, classLoader));
            } catch (ClassNotFoundException | LinkageError e) {
                log.warn("cannot load {}: {}", name, e.toString());
            }
        }
        return interfaces;
    }

    protected Object createProxyBean(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory) {
        return createProxyBean(beanClass, beanFactory, beanFactory.getBeanClassLoader());
    }

    protected Object createProxyBean(Class<?> beanClass, ConfigurableListableBeanFactory beanFactory, ClassLoader classLoader) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(beanClass);
        proxyFactory.addAdvice(new RestExecutorMethodHandler(beanFactory));
        return proxyFactory.getProxy(classLoader);
    }

    protected String variableName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

}

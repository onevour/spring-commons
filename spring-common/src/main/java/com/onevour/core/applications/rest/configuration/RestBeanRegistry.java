package com.onevour.core.applications.rest.configuration;


import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mendaftarkan setiap interface turunan {@link RestRepository} di package aplikasi
 * sebagai bean, dengan pola yang sama seperti Spring Data:
 * <ol>
 *   <li>package diambil dari {@link AutoConfigurationPackages} (String), bukan dari
 *       {@code Class.forName} bean {@code @SpringBootApplication} yang bisa berupa class CGLIB;</li>
 *   <li>interface ditemukan dengan membaca bytecode (ASM) tanpa memuat class;</li>
 *   <li>yang didaftarkan adalah {@link BeanDefinition} berisi {@link RestRepositoryFactoryBean}
 *       dengan nama interface, sehingga class baru dimuat saat bean dibuat lewat classloader context.</li>
 * </ol>
 */
@Slf4j
@Configuration
public class RestBeanRegistry extends BasicRestBuilder implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!(registry instanceof ConfigurableListableBeanFactory)) {
            log.warn("BeanDefinitionRegistry {} is not a ConfigurableListableBeanFactory, RestRepository scan skipped", registry.getClass().getName());
            return;
        }
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) registry;
        ClassLoader classLoader = beanFactory.getBeanClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        Set<String> packages = basePackages(beanFactory);
        if (packages.isEmpty()) {
            log.warn("no base package found (AutoConfigurationPackages / @SpringBootApplication), RestRepository scan skipped");
            return;
        }
        log.debug("scan RestRepository in packages {} with classloader {}", packages, classLoader);

        Set<String> names = scanInterfaceNames(RestRepository.class, classLoader, packages);
        int registered = 0;
        for (String className : names) {
            String beanName = beanName(registry, className);
            if (beanName == null) continue;
            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(RestRepositoryFactoryBean.class)
                    .addConstructorArgValue(className)
                    .setLazyInit(false)
                    .getBeanDefinition();
            registry.registerBeanDefinition(beanName, definition);
            registered++;
            log.trace("register RestRepository {} as bean {}", className, beanName);
        }
        log.info("RestRepository found {}", registered);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // semua pekerjaan dilakukan di postProcessBeanDefinitionRegistry
    }

    /**
     * Package aplikasi, seperti Spring Data: dari {@link AutoConfigurationPackages}.
     * Fallback: package dari nama class bean {@code @SpringBootApplication}
     * (operasi String saja, tidak memuat class; suffix CGLIB tidak mengubah package).
     */
    private Set<String> basePackages(ConfigurableListableBeanFactory beanFactory) {
        Set<String> packages = new LinkedHashSet<>();
        if (AutoConfigurationPackages.has(beanFactory)) {
            packages.addAll(AutoConfigurationPackages.get(beanFactory));
        }
        if (!packages.isEmpty()) {
            return packages;
        }
        for (String beanName : beanFactory.getBeanNamesForAnnotation(SpringBootApplication.class)) {
            String className = beanFactory.getBeanDefinition(beanName).getBeanClassName();
            if (className == null) continue;
            packages.add(ClassUtils.getPackageName(className));
        }
        return packages;
    }

    /**
     * Setara {@code Class.getSimpleName()} tanpa memuat class: bagian setelah '.' atau '$' terakhir.
     */
    private static String simpleName(String className) {
        int index = Math.max(className.lastIndexOf('.'), className.lastIndexOf('$'));
        return index < 0 ? className : className.substring(index + 1);
    }

    /**
     * Nama bean = simple name dengan huruf pertama kecil (perilaku lama).
     * Jika sudah dipakai interface lain, pakai nama lengkap supaya tidak saling timpa.
     */
    private String beanName(BeanDefinitionRegistry registry, String className) {
        String shortName = variableName(simpleName(className));
        if (!registry.containsBeanDefinition(shortName)) {
            return shortName;
        }
        BeanDefinition existing = registry.getBeanDefinition(shortName);
        if (RestRepositoryFactoryBean.class.getName().equals(existing.getBeanClassName())
                && existing.getConstructorArgumentValues().getIndexedArgumentValue(0, String.class) != null
                && className.equals(existing.getConstructorArgumentValues().getIndexedArgumentValue(0, String.class).getValue())) {
            log.debug("RestRepository {} already registered as {}", className, shortName);
            return null;
        }
        if (registry.containsBeanDefinition(className)) {
            log.debug("RestRepository {} already registered", className);
            return null;
        }
        log.warn("bean name {} already used by {}, register RestRepository {} with its full name", shortName, existing.getBeanClassName(), className);
        return className;
    }
}

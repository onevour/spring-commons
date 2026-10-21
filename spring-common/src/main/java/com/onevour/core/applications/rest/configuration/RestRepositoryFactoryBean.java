package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * FactoryBean untuk satu interface {@code RestRepository}.
 * <p>
 * Mengikuti pola Spring Data: yang didaftarkan ke registry hanya <b>nama</b> interface (String).
 * Class baru dimuat saat bean dibuat, lewat classloader milik {@link BeanFactory}
 * ({@link BeanClassLoaderAware}), bukan lewat classloader library. Dengan begitu
 * interface aplikasi tetap ditemukan saat Spring Boot Devtools aktif
 * (RestartClassLoader), dan tipe bean sama dengan tipe yang di-inject ke service.
 */
public class RestRepositoryFactoryBean implements FactoryBean<Object>, BeanClassLoaderAware, BeanFactoryAware {

    private final String repositoryInterfaceName;

    private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    private ConfigurableListableBeanFactory beanFactory;

    private Class<?> repositoryInterface;

    private Object proxy;

    public RestRepositoryFactoryBean(String repositoryInterfaceName) {
        Assert.hasText(repositoryInterfaceName, "repository interface name cannot be empty");
        this.repositoryInterfaceName = repositoryInterfaceName;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
                "RestRepositoryFactoryBean requires a ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public synchronized Object getObject() {
        if (proxy == null) {
            Class<?> type = repositoryInterface();
            ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.addInterface(type);
            proxyFactory.addAdvice(new RestExecutorMethodHandler(beanFactory));
            // proxy dibuat di classloader context, sama dengan tempat interface dimuat
            proxy = proxyFactory.getProxy(classLoader);
        }
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return repositoryInterface();
        } catch (IllegalStateException e) {
            // tipe belum bisa ditentukan (mis. dipanggil terlalu awal); Spring akan mencoba lagi nanti
            return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getRepositoryInterfaceName() {
        return repositoryInterfaceName;
    }

    private synchronized Class<?> repositoryInterface() {
        if (repositoryInterface == null) {
            try {
                Class<?> type = ClassUtils.forName(repositoryInterfaceName, classLoader);
                Assert.isTrue(type.isInterface(), "RestRepository " + repositoryInterfaceName + " must be an interface");
                repositoryInterface = type;
            } catch (ClassNotFoundException | LinkageError e) {
                throw new IllegalStateException("cannot load RestRepository interface " + repositoryInterfaceName
                        + " with classloader " + classLoader, e);
            }
        }
        return repositoryInterface;
    }
}

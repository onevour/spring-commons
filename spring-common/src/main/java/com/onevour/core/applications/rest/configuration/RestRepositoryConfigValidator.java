package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.annotations.*;
import com.onevour.core.applications.rest.exceptions.RestConfigNotFoundException;
import com.onevour.core.applications.rest.exceptions.RestConfigValidationException;
import com.onevour.core.applications.rest.repository.RestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validasi konfigurasi semua RestRepository saat startup, seperti Spring memvalidasi
 * {@code @ConfigurationProperties}: setiap {@code key}/{@code url} pada {@code @Get}, {@code @Post},
 * {@code @Put}, {@code @Patch}, {@code @Delete} di-resolve (properti, database, atau hardcode) dan
 * gabungannya harus URL http(s) yang valid. Satu saja gagal, aplikasi tidak jadi up dan semua
 * masalah dilaporkan sekaligus lewat {@link RestConfigValidationException}.
 * <p>
 * Berjalan di {@link SmartInitializingSingleton#afterSingletonsInstantiated()}: setelah semua bean
 * (termasuk datasource dan {@code RestConfigBaseKey}) siap, tetapi sebelum web server menerima request.
 * <p>
 * Matikan dengan {@code spring-common.rest.validate-on-startup=false}.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring-common.rest", name = "validate-on-startup", havingValue = "true", matchIfMissing = true)
public class RestRepositoryConfigValidator implements SmartInitializingSingleton, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        RestConfigValueResolver resolver = new RestConfigValueResolver(beanFactory);
        List<String> problems = new ArrayList<>();
        int repositories = 0;
        int methods = 0;

        for (String beanName : beanFactory.getBeanNamesForType(RestRepository.class, true, true)) {
            Class<?> type = beanFactory.getType(beanName);
            if (Objects.isNull(type) || !type.isInterface() || !RestRepository.class.isAssignableFrom(type)) {
                continue;
            }
            repositories++;
            for (Method method : type.getMethods()) {
                String[] keyUrl = keyUrl(method);
                if (Objects.isNull(keyUrl)) continue;
                methods++;
                String where = type.getName() + "#" + method.getName() + "()";
                String detail = " (key='" + keyUrl[0] + "', url='" + keyUrl[1] + "')";
                try {
                    String baseUrl = resolver.baseUrl(keyUrl[0], keyUrl[1]);
                    if (!isValidUrl(baseUrl)) {
                        problems.add(where + ": resolved url '" + baseUrl + "' is not a valid http(s) url" + detail);
                    } else {
                        log.trace("{} -> {}", where, baseUrl);
                    }
                } catch (RestConfigNotFoundException e) {
                    problems.add(where + ": " + e.getMessage() + detail);
                }
            }
        }

        if (!problems.isEmpty()) {
            throw new RestConfigValidationException(problems);
        }
        log.info("RestRepository config validated: {} method(s) in {} repository(ies)", methods, repositories);
    }

    private String[] keyUrl(Method method) {
        Get get = method.getAnnotation(Get.class);
        if (Objects.nonNull(get)) return new String[]{get.key(), get.url()};
        Post post = method.getAnnotation(Post.class);
        if (Objects.nonNull(post)) return new String[]{post.key(), post.url()};
        Put put = method.getAnnotation(Put.class);
        if (Objects.nonNull(put)) return new String[]{put.key(), put.url()};
        Patch patch = method.getAnnotation(Patch.class);
        if (Objects.nonNull(patch)) return new String[]{patch.key(), patch.url()};
        Delete delete = method.getAnnotation(Delete.class);
        if (Objects.nonNull(delete)) return new String[]{delete.key(), delete.url()};
        return null;
    }

    /** Path variable {@code {x}} diganti placeholder dulu, lalu harus punya scheme http/https dan host. */
    static boolean isValidUrl(String url) {
        if (Objects.isNull(url) || url.isBlank()) return false;
        String probe = url.replaceAll("\\{[^}]*}", "x");
        try {
            URI uri = new URI(probe);
            String scheme = uri.getScheme();
            return Objects.nonNull(scheme) && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && Objects.nonNull(uri.getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }

}

package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.exceptions.RestConfigNotFoundException;
import com.onevour.core.applications.rest.model.RestConfigBaseKey;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * Me-resolve nilai {@code key} dan {@code url} anotasi RestRepository. Dipakai oleh
 * {@code RestExecutorMethodHandler} saat method dipanggil dan oleh
 * {@link RestRepositoryConfigValidator} saat startup, supaya aturannya satu:
 * <ul>
 *   <li>{@code ${nama.properti}}: dari {@link Environment} (mendukung default {@code ${x:default}});</li>
 *   <li>{@code D{KUNCI}} / {@code d{KUNCI}}: dari bean {@link RestConfigBaseKey};</li>
 *   <li>diawali {@code http}: dipakai apa adanya;</li>
 *   <li>teks lain pada {@code key}: dianggap kunci database; pada {@code url}: dipakai apa adanya.</li>
 * </ul>
 * Nilai yang tidak ditemukan dilempar sebagai {@link RestConfigNotFoundException}.
 */
public class RestConfigValueResolver {

    private final BeanFactory beanFactory;

    public RestConfigValueResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /** {@code key} + {@code url}, sudah di-trim. */
    public String baseUrl(String key, String url) {
        return (nullToEmpty(resolveKey(key)) + nullToEmpty(resolveUrl(url))).trim();
    }

    public String resolveKey(String key) {
        return resolve(key, true);
    }

    public String resolveUrl(String url) {
        return resolve(url, false);
    }

    public String resolve(String value, boolean tryAsKeyDb) {
        if (Objects.isNull(value)) {
            return null;
        }
        String valueTrim = value.trim();
        if (valueTrim.isEmpty()) {
            return valueTrim;
        }
        if (valueTrim.contains("${")) {
            try {
                return beanFactory.getBean(Environment.class).resolveRequiredPlaceholders(valueTrim).trim();
            } catch (IllegalArgumentException e) {
                throw new RestConfigNotFoundException("property " + valueTrim + " not found in Environment (application.properties/yml atau environment variable)", e);
            }
        }
        if ((valueTrim.startsWith("D{") || valueTrim.startsWith("d{")) && valueTrim.endsWith("}")) {
            String key = valueTrim.substring(2, valueTrim.length() - 1).trim();
            return fromDatabase(key, valueTrim);
        }
        if (valueTrim.startsWith("http")) {
            return valueTrim;
        }
        if (tryAsKeyDb) {
            return fromDatabase(valueTrim, valueTrim);
        }
        return valueTrim;
    }

    private String fromDatabase(String key, String placeholder) {
        RestConfigBaseKey config = beanFactory.getBeanProvider(RestConfigBaseKey.class).getIfAvailable();
        if (Objects.isNull(config)) {
            throw new RestConfigNotFoundException("database key " + placeholder + " cannot be resolved: no RestConfigBaseKey bean, implement it or use ${...}/http url");
        }
        String value;
        try {
            value = config.getValue(key);
        } catch (RuntimeException e) {
            throw new RestConfigNotFoundException("database key '" + key + "' not found: RestConfigBaseKey threw " + e, e);
        }
        if (Objects.isNull(value) || value.trim().isEmpty()) {
            throw new RestConfigNotFoundException("database key '" + key + "' not found: RestConfigBaseKey returned null/empty");
        }
        return value.trim();
    }

    private static String nullToEmpty(String value) {
        return Objects.isNull(value) ? "" : value;
    }

}

package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.repository.RestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Memastikan interface RestRepository di package aplikasi terdaftar sebagai bean
 * lewat RestRepositoryFactoryBean dan tipenya sama dengan yang di-inject.
 */
@SpringBootTest(classes = RestBeanRegistryTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RestBeanRegistryTest {

    @SpringBootApplication
    @ComponentScan(excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = RestBeanRegistryTest.class))
    static class App {
    }

    public interface SampleRestRepository extends RestRepository {
        @Get(url = "http://localhost/sample")
        String call();
    }

    // hierarki: Child -> Base -> RestRepository, harus tetap ditemukan lewat metadata
    public interface BaseRestRepository extends RestRepository {
    }

    public interface ChildRestRepository extends BaseRestRepository {
        @Get(url = "http://localhost/child")
        String call();
    }

    @Autowired
    ApplicationContext context;

    @Autowired
    SampleRestRepository sampleRestRepository;

    @Test
    void repositoryRegisteredAsFactoryBeanAndInjectable() {
        assertNotNull(sampleRestRepository);
        assertTrue(context.containsBean("sampleRestRepository"));
        assertTrue(context.containsBean("childRestRepository"));
        // bean yang di-inject adalah proxy dari interface versi classloader context
        assertSame(SampleRestRepository.class, context.getType("sampleRestRepository"));
        assertTrue(ChildRestRepository.class.isInstance(context.getBean("childRestRepository")));
        assertTrue(context.containsBean("baseRestRepository"));
        // yang terdaftar adalah FactoryBean dengan nama interface, bukan singleton proxy langsung
        Object factory = context.getBean("&sampleRestRepository");
        assertTrue(factory instanceof RestRepositoryFactoryBean);
        assertEquals(SampleRestRepository.class.getName(), ((RestRepositoryFactoryBean) factory).getRepositoryInterfaceName());
        // marker interface sendiri tidak didaftarkan
        assertFalse(context.containsBean("restRepository"));
    }
}

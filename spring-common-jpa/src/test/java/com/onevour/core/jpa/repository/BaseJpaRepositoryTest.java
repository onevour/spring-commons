package com.onevour.core.jpa.repository;

import com.onevour.core.applications.base.BaseEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Method default BaseJpaRepository mendelegasikan ke findAll(spec, pageable) dengan PageRequest yang benar. */
class BaseJpaRepositoryTest {

    static class Sample extends BaseEntity {
    }

    interface SampleRepository extends BaseJpaRepository<Sample, Long> {
    }

    @Test
    void findAllWithPageSizeSortBuildsPageRequest() {
        SampleRepository repo = mock(SampleRepository.class);
        Specification<Sample> spec = (root, q, cb) -> null;
        List<Sample> content = Arrays.asList(new Sample(), new Sample());
        when(repo.findAll(eq(spec), any(Pageable.class))).thenReturn(new PageImpl<>(content));
        when(repo.findAll(eq(spec), anyInt(), anyInt(), any(Sort.class))).thenCallRealMethod();
        when(repo.findAllSpec(eq(spec), any(Pageable.class))).thenCallRealMethod();

        List<Sample> result = repo.findAll(spec, 2, 10, Sort.by("name"));

        assertEquals(content, result);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(eq(spec), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
        assertEquals(Sort.by("name"), captor.getValue().getSort());
    }
}

package com.onevour.core.jpa.repository;

import com.onevour.core.applications.base.BaseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    default List<T> findAll(Specification<T> spec, int page, int size, Sort sort) {
        return findAllSpec(spec, PageRequest.of(page, size, sort));
    }

    default List<T> findAllSpec(Specification<T> spec, Pageable pageable) {
        Slice<T> slice = findAll(spec, pageable);
        return slice.getContent();
    }

}

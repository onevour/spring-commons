package com.onevour.core.applications.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<T> findAll(Specification<T> spec, int offset, int maxResults, Sort sort);

    List<T> findAllSpec(Specification<T> spec, PageRequest pageRequest);

}

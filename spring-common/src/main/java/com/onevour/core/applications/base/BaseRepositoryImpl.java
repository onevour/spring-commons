package com.onevour.core.applications.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public List<T> findAll(Specification<T> spec, int offset, int maxResults) {
        return findAll(spec, offset, maxResults, Sort.unsorted());
    }

    @Override
    public List<T> findAllSpec(Specification<T> spec, PageRequest pageRequest) {
        return findAll(spec, Math.toIntExact(pageRequest.getOffset()), pageRequest.getPageSize(), pageRequest.getSort());
    }


    public List<T> findAll(Specification<T> spec, int offset, int maxResults, Sort sort) {
        TypedQuery<T> query = getQuery(spec, sort);

        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero!");
        }
        if (maxResults < 1) {
            throw new IllegalArgumentException("Max results must not be less than one!");
        }
        query.setFirstResult(offset);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

}

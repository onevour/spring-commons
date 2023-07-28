package com.onevour.core.applications.base;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class BaseSpec {

    public boolean hasNull(Object... values) {
        for (Object o : values) {
            if (Objects.isNull(o)) return true;
            if (o instanceof String) {
                String value = (String) o;
                if (value.trim().isEmpty()) return true;
            }
        }
        return false;
    }

    public boolean hasNotNull(Object... values) {
        for (Object o : values) {
            if (Objects.isNull(o)) return false;
            if (o instanceof String) {
                String value = (String) o;
                if (value.trim().isEmpty()) return false;
            }
        }
        return true;
    }

    public String paramLike(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    public String paramStartWith(String value) {
        StringBuilder sb = new StringBuilder();
        // sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    public String paramEndWith(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
//        sb.append("%");
        return sb.toString();
    }

    public boolean isGTZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value > 0;
    }

    public boolean isGTEqZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value >= 0;
    }

    public boolean isGTZero(Long value) {
        if (Objects.isNull(value)) return false;
        return value > 0;
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public <T> Set<T> findDuplicateBySetAdd(List<T> list) {
        Set<T> items = new HashSet<>();
        return list.stream().filter(n -> !items.add(n)) // Set.add() returns false if the element was already in the set.
                .collect(Collectors.toSet());

    }

//    protected <T> List<javax.persistence.criteria.Predicate> notDeleted(Root<T> root, CriteriaBuilder criteriaBuilder) {
//        List<javax.persistence.criteria.Predicate> predicateList = new ArrayList<>();
//        javax.persistence.criteria.Predicate productNotDeleted = criteriaBuilder.equal(root.get("deleted"), false);
//        predicateList.add(productNotDeleted);
//        return predicateList;
//    }

}

package com.onevour.core.applications.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BaseSpec {

    protected static boolean hasNull(Object... values) {
        for (Object o : values) {
            if (Objects.isNull(o)) return true;
            if (o instanceof String) {
                String value = (String) o;
                if (value.trim().isEmpty()) return true;
            }
        }
        return false;
    }

    protected static boolean hasNotNull(Object... values) {
        for (Object o : values) {
            if (Objects.isNull(o)) return false;
            if (o instanceof String) {
                String value = (String) o;
                if (value.trim().isEmpty()) return false;
            }
        }
        return true;
    }

    protected static String paramLike(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    protected static String paramStartWith(String value) {
        StringBuilder sb = new StringBuilder();
        // sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    protected static String paramEndWith(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
//        sb.append("%");
        return sb.toString();
    }

    protected static boolean isGTZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value > 0;
    }

    protected static boolean isGTEqZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value >= 0;
    }

    protected static boolean isGTZero(Long value) {
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

    protected <T> List<javax.persistence.criteria.Predicate> notDeleted(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<javax.persistence.criteria.Predicate> predicateList = new ArrayList<>();
        javax.persistence.criteria.Predicate productNotDeleted = criteriaBuilder.equal(root.get("deleted"), false);
        predicateList.add(productNotDeleted);
        return predicateList;
    }

}

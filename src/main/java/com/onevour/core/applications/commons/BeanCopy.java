package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;

@Slf4j
@SuppressWarnings({"unchecked"})
public class BeanCopy {

    public static <T> T copy(Object source, Class<T> tClass) {
        return copy(source, tClass, new String[]{});
    }

    public static <T> T copy(Object source, Class<T> tClass, String... exclude) {
        if (Objects.isNull(source)) throw new NullPointerException();
        T o = BeanUtils.instantiateClass(tClass);
        BeanUtils.copyProperties(source, o, exclude);
        return o;
    }

    public static <E, T> List<T> copyCollection(List<E> source, Class<T> tClass, String... exclude) {
        return copyCollection(source, new ArrayList<>(), tClass, exclude);
    }

    public static <E, T> List<T> copyCollection(List<E> sources, List<T> destination, Class<T> tClass, String... exclude) {
        if (Objects.isNull(sources)) return new ArrayList<>();
        for (Object source : sources) {
            T o = copy(source, tClass, exclude);
            destination.add(o);
        }
        return destination;
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }



}

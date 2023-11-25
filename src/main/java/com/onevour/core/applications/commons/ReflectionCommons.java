package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

@Slf4j
public class ReflectionCommons {

    public static boolean isParameterizedTypeReference(Type genericReturnType) {

        if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                log.debug("Type Argument: {}", typeArgument);
            }
            return typeArguments.length > 0;
        }
        return false;
    }

    public static <T> ParameterizedTypeReference<T> parameterized(Type type) {
        return new ParameterizedTypeReference<T>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public Type getType() {
                return type;
            }
        };
    }

    public static Type getGenericParameterType(Type type, int index) {
        if (!ParameterizedType.class.isInstance(type)) {
            return null;
        }
        final Type[] genericParameterTypeList = getGenericParameterTypes(type);
        if (genericParameterTypeList.length == 0 || genericParameterTypeList.length < index) {
            return null;
        }
        return genericParameterTypeList[index];
    }

    @SuppressWarnings("RedundantClassCall")
    public static Type[] getGenericParameterTypes(Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType paramType = ParameterizedType.class.cast(type);
            return paramType.getActualTypeArguments();
        }
        if (GenericArrayType.class.isInstance(type)) {
            final GenericArrayType arrayType = GenericArrayType.class.cast(type);
            return getGenericParameterTypes(arrayType.getGenericComponentType());
        }
        return new Type[0];
    }

    public static <T> Type typeOf(ParameterizedTypeReference<T> parameterized) {
        if (Objects.isNull(parameterized)) return null;
        return parameterized.getType();
    }

}

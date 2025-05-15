package com.onevour.core.applications.configurations;


import com.onevour.core.applications.annotations.ConverterResolver;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.onevour.core.applications.configurations")
public class MethodHandlerConverter {

    @Autowired
    BeanFactory beanFactory;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object producerConverter(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethodFromJoinPoint(joinPoint);
        if (Objects.isNull(method)) {
            return joinPoint.proceed();
        }
        Object[] arguments = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int sizeAnnotation = parameterAnnotations.length;
        if (sizeAnnotation == 0) return joinPoint.proceed();
        for (int index = 0; index < sizeAnnotation; index++) {
            updateBeanConverter(arguments, parameterAnnotations[index], index);
        }
        return joinPoint.proceed(arguments);
    }

    private Method getMethodFromJoinPoint(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        for (Method method : targetClass.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == joinPoint.getArgs().length) {
                return method;
            }
        }
        return null;
    }

    private void updateBeanConverter(Object[] args, Annotation[] annotations, int index) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ConverterResolver) {
                args[index] = beanFactory.getBean(args[index].getClass());
            }
        }
    }

}

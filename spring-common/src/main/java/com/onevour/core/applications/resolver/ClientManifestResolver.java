package com.onevour.core.applications.resolver;

import com.onevour.core.applications.session.ClientManifest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ClientManifestResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        List<Class<?>> interfaces = Arrays.asList(parameter.getParameterType().getInterfaces());
        return interfaces.contains(ClientManifest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (Objects.isNull(httpServletRequest)) {
            log.debug("no session exist");
            return null;
        }
        Object value = httpServletRequest.getAttribute("CLIENT_MANIFEST");
        if (Objects.isNull(value)) {
            log.debug("get no sig value exist!");
            return null;
        }
        if (supportsParameter(parameter)) {
            log.debug("return value session");
            return value;
        }
        return null;
    }

}
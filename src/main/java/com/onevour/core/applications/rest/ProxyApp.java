package com.onevour.core.applications.rest;


import com.onevour.core.applications.rest.handler.RestExecutorMethodHandler;
import com.onevour.core.applications.rest.services.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

public class ProxyApp {

    private static final Logger log = LoggerFactory.getLogger(ProxyApp.class);

    public static void main(String[] args) {

        ProxyFactory proxyFactory = new ProxyFactory();
        // use scanner spring
        proxyFactory.addInterface(AuthRepository.class);
        // advisor
        proxyFactory.addAdvice(new RestExecutorMethodHandler());

        AuthRepository authRepository = (AuthRepository) proxyFactory.getProxy();

        //
        for (int i = 0; i < 1; i++) {
//            authRepository.auth(new BodyRequest("meat-and-filler"));
//            List<String> response = authRepository.auth(new BodyRequest("meat-and-filler"));
//            for (String s : response) {
//                log.info("response main {}", s);
//            }
        }

    }
}

package com.onevour.core.configurations;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class EntityManagerProvider {

    private static EntityManager em;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        EntityManagerProvider.em = em;
    }

    public static EntityManager get() {
        return em;
    }
}

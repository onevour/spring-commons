package com.onevour.core.applications.commons;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateEventIdExtractor {

    public Map<String, Object> extractPrimaryKey(PostUpdateEvent event) {

        Map<String, Object> pk = new LinkedHashMap<>();
        Serializable idObj = event.getId();
        AbstractEntityPersister aep = (AbstractEntityPersister) event.getPersister();

        // simple @Id
        if (!aep.getIdentifierType().isComponentType()) {
            String col = aep.getIdentifierColumnNames()[0];
            pk.put(col.toLowerCase(), idObj);
//            pk.put("original_pk_" + col.toLowerCase(), idObj);
            return pk;
        }

        // @EmbeddedId
        if (isEmbeddedId(aep)) {
            return extractEmbeddedId(aep, idObj);
        }

        // @IdClass
        if (isIdClass(aep)) {
            return extractIdClass(aep, idObj);
        }

        return pk; // fallback
    }

    private boolean isEmbeddedId(AbstractEntityPersister aep) {
        Type idType = aep.getIdentifierType();

        // not a composite → definitely not embedded
        if (!idType.isComponentType()) return false;

        // embeddedId has identifierPropertyName
        return aep.getIdentifierPropertyName() != null;
    }

    private boolean isIdClass(AbstractEntityPersister aep) {
        Type idType = aep.getIdentifierType();

        // not a composite → no IdClass
        if (!idType.isComponentType()) return false;

        // IdClass has NO identifierPropertyName
        return aep.getIdentifierPropertyName() == null;
    }

    private Map<String, Object> extractEmbeddedId(AbstractEntityPersister aep, Serializable idObj) {

        Map<String, Object> pk = new LinkedHashMap<>();

        Class<?> embClass = idObj.getClass();
        String idProp = aep.getIdentifierPropertyName();  // ex: "id"

        for (Field f : embClass.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object value = f.get(idObj);

                String propPath = idProp + "." + f.getName(); // ex: "id.branchId"

                String[] cols = aep.getPropertyColumnNames(propPath);
                String col = (cols != null && cols.length > 0) ? cols[0] : f.getName();

                pk.put(col.toLowerCase(), value);
//                pk.put("original_pk_" + col.toLowerCase(), value);
            } catch (Exception ignored) {
            }
        }

        return pk;
    }

    private Map<String, Object> extractIdClass(AbstractEntityPersister aep, Serializable idObj) {

        Map<String, Object> pk = new LinkedHashMap<>();
        Class<?> idClass = idObj.getClass();

        for (Field f : idClass.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object value = f.get(idObj);
                String[] cols = aep.getPropertyColumnNames(f.getName());
                String col = (cols != null && cols.length > 0) ? cols[0] : f.getName();
                pk.put(col.toLowerCase(), value);
//                pk.put("original_pk_" + col.toLowerCase(), value);
            } catch (Exception ignored) {
            }
        }

        return pk;
    }
}

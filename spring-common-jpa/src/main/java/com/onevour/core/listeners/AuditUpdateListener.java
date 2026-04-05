package com.onevour.core.listeners;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import com.onevour.core.applications.commons.DeleteEventIdExtractor;
import com.onevour.core.applications.commons.UpdateEventIdExtractor;
import com.onevour.core.configurations.EntityManagerProvider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class AuditUpdateListener extends BaseAuditListener implements PostUpdateEventListener, PreDeleteEventListener {

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event == null || event.getEntity() == null) {
            return;
        }
        Object entity = event.getEntity();
        if (!(entity instanceof BaseEntity)) {
            return;
        }
        BaseEntity baseEntity = (BaseEntity) entity;
        if (!entity.getClass().isAnnotationPresent(EntityHistory.class)) {
            return;
        }

        Class<?> clazz = entity.getClass();
        String table = resolveTableName(clazz);
        String hisTable = table + "_his";

        Map<String, Object> oldData = extractUpdateState(event);
        if (oldData.isEmpty()) {
            return;
        }

        // PersistentBag
        Map<String, Object> primaryKeys = new UpdateEventIdExtractor().extractPrimaryKey(event);

        oldData.putAll(primaryKeys);
        oldData.put("audit_id", UUID.randomUUID());
        oldData.put("audit_type", Boolean.TRUE.equals(baseEntity.getDeleted()) ? "DELETE" : "UPDATE");
        oldData.put("audit_date", LocalDateTime.now());

        Set<String> removeKeys = new HashSet<>();
        for (Map.Entry<String, Object> entry : oldData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (Objects.isNull(value)) continue;
            if (value instanceof PersistentBag) {
                removeKeys.add(key);
            }
            if (value instanceof PersistentSet) {
                removeKeys.add(key);
            }
        }
        for (String key : removeKeys) {
            oldData.remove(key);
        }
        // execute insert his
        EntityManager em = EntityManagerProvider.get();
        Session session = em.unwrap(Session.class);
        String auditType = Boolean.TRUE.equals(baseEntity.getDeleted()) ? "SOFT_DELETE" : "UPDATE";
        insertHistory(session, table, hisTable, oldData, primaryKeys, auditType);
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        if (event == null || event.getEntity() == null) {
            return false;
        }
        Object entity = event.getEntity();
        if (!(entity instanceof BaseEntity)) {
            return false;
        }
        if (!entity.getClass().isAnnotationPresent(EntityHistory.class)) {
            return false;
        }
        Class<?> clazz = entity.getClass();
        String tableLive = resolveTableName(clazz);
        String tableHis = tableLive + "_his";

        Map<String, Object> oldData = extractDeletedState(event);
        if (oldData.isEmpty()) {
            return false;
        }

        Map<String, Object> primaryKeys = new DeleteEventIdExtractor().extractPrimaryKey(event);
        oldData.putAll(primaryKeys);
        oldData.put("is_deleted", true);
        oldData.put("audit_id", UUID.randomUUID());
        oldData.put("audit_type", "DELETE");
        oldData.put("audit_date", LocalDateTime.now());

        Set<String> removeKeys = new HashSet<>();
        for (Map.Entry<String, Object> entry : oldData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (Objects.isNull(value)) continue;
            if (value instanceof PersistentBag) {
                removeKeys.add(key);
            }
            if (value instanceof PersistentSet) {
                removeKeys.add(key);
            }
        }
        for (String key : removeKeys) {
            oldData.remove(key);
        }
        // execute insert his
        EntityManager em = EntityManagerProvider.get();
        Session session = em.unwrap(Session.class);
        insertHistory(session, tableLive, tableHis, oldData, primaryKeys, "HARD_DELETE");
        return false;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

}

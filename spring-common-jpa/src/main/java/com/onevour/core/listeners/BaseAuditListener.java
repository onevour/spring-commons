package com.onevour.core.listeners;

import com.onevour.core.applications.exceptions.AuditHistoryListenerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseAuditListener {

    @Autowired(required = false)
    AuditorAware<String> auditorAware;

    protected final Set<String> keyword = new HashSet<>(Arrays.asList("key", "value", "order", "group"));

    protected final PhysicalNamingStrategyStandardImpl naming = new PhysicalNamingStrategyStandardImpl();

    protected Map<String, Object> extractDeletedState(PreDeleteEvent event) {

        Map<String, Object> map = new LinkedHashMap<>();

        Object[] deletedState = event.getDeletedState();
        String[] props = event.getPersister().getPropertyNames();
        Class<?> clazz = event.getEntity().getClass();

        if (deletedState == null || props == null) {
            return map; // no data
        }

        AbstractEntityPersister aep = null;
        if (event.getPersister() instanceof AbstractEntityPersister) {
            aep = (AbstractEntityPersister) event.getPersister();
        }

        for (int i = 0; i < props.length; i++) {

            String prop = props[i];
            Object value = deletedState[i];

            if (value instanceof PersistentBag || value instanceof PersistentSet) {
                continue;
            }
            // Skip collection (@OneToMany, @ManyToMany)
            if (value instanceof PersistentCollection) {
                continue;
            }

            String col = null;

            // ---------------------------------------------------------
            // 1) Hibernate column name via Persister (paling akurat)
            // ---------------------------------------------------------
            if (aep != null) {
                try {
                    String[] cols = aep.getPropertyColumnNames(i);
                    if (cols != null && cols.length > 0 && cols[0] != null) {
                        col = cols[0];
                        if (keyword.contains(col.toLowerCase())) {
                            col = "`" + col + "`";
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            // ---------------------------------------------------------
            // 2) Field @Column(name=...)
            // ---------------------------------------------------------
            if (col == null) {
                Field f = findField(clazz, prop);
                if (f != null) {
                    Column ann = f.getAnnotation(Column.class);
                    if (ann != null && !ann.name().isEmpty()) {
                        col = ann.name();
                        if (keyword.contains(col.toLowerCase())) {
                            col = "`" + col + "`";
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // 3) PhysicalNamingStrategy fallback (Spring Boot 2.7)
            // ---------------------------------------------------------
            if (col == null) {
                Identifier id = Identifier.toIdentifier(prop);
                Identifier phys = naming.toPhysicalColumnName(id, null);
                col = phys.getText(); // contoh: modifiedDate -> modified_date
                if (keyword.contains(col.toLowerCase())) {
                    col = "`" + col + "`";
                }
            }

            // 4) fallback terakhir – pakai prop apa adanya
            if (col == null) {
                col = prop;
                if (keyword.contains(col.toLowerCase())) {
                    col = "`" + col + "`";
                }
            }

            if (isManyToOne(clazz, prop)) {
                log.trace("delete value contains many to one relation, extract value");
                Object fkId = extractEntityId(value, event.getSession());
                map.put(col, fkId);
            } else {
                map.put(col, value);
            }

        }
        if (Objects.nonNull(auditorAware)) {
            map.put("modified_by", auditorAware.getCurrentAuditor().orElse(null));
            map.put("modified_date", new Date());
        }

        return map;
    }

    // ---------------------------
    // TABLE NAME
    // ---------------------------
    protected String resolveTableName(Class<?> clazz) {
        Table t = clazz.getAnnotation(Table.class);
        if (t != null && !t.name().isEmpty()) return t.name();
        return clazz.getSimpleName().toLowerCase();
    }

    // ---------------------------
    // EXTRACT OLD STATE
    // ---------------------------
    protected Map<String, Object> extractUpdateState(PostUpdateEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();

        EntityPersister p = event.getPersister();
        AbstractEntityPersister aep = (p instanceof AbstractEntityPersister) ? (AbstractEntityPersister) p : null;
        String[] props = p.getPropertyNames();
        Object[] old = event.getOldState();
        if (old == null) {
            return map;
        }

        Class<?> clazz = event.getEntity().getClass();

        for (int i = 0; i < props.length; i++) {

            String prop = props[i];
            Object value = old[i];
            String col = null;

            // 1) Hibernate column name
            if (aep != null) {
                try {
                    String[] cols = aep.getPropertyColumnNames(i);
                    if (cols != null && cols.length > 0 && cols[0] != null) {
                        col = cols[0];
                    }
                } catch (Exception ignored) {
                }
            }

            // 2) Try field @Column
            if (col == null) {
                Field f = findField(clazz, prop);
                if (f != null && f.isAnnotationPresent(Column.class)) {
                    String nn = f.getAnnotation(Column.class).name();
                    if (!nn.isEmpty()) col = nn;
                }
            }

            // 3) Apply physical naming strategy (same as Boot 2.7)
            if (col == null) {
                Identifier id = Identifier.toIdentifier(prop);
                Identifier phys = naming.toPhysicalColumnName(id, null);
                col = phys.getText();
            }

            if (isManyToOne(clazz, prop)) {
                log.trace("update value is contains many to one relation, extract value");
                Object fkId = extractEntityId(value, event.getSession());
                map.put(col, fkId);
            } else {
                map.put(col, value);
            }
        }

        return map;
    }

    protected Field findField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null && !c.equals(Object.class)) {
            try {
                return c.getDeclaredField(name);
            } catch (Exception ignored) {
            }
            c = c.getSuperclass();
        }
        return null;
    }

    // ---------------------------
    // CREATE HISTORY TABLE ON UPDATE
    // ---------------------------
    protected void createHistoryTableIfNeeded(Session session, String base, String his, Map<String, Object> primaryKeys, PostUpdateEvent event) {

        final boolean[] exists = {false};

        session.doWork(conn -> {
            try {
                try (PreparedStatement ps = conn.prepareStatement("SELECT to_regclass(?)")) {
                    ps.setString(1, his);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getString(1) != null) {
                            exists[0] = true;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });

        if (exists[0]) return;

        session.doWork(conn -> {
            try {
                try (Statement st = conn.createStatement()) {
                    // copy
                    st.execute("CREATE TABLE " + his + " (LIKE " + base + " INCLUDING DEFAULTS INCLUDING GENERATED)");

                    // drop original PK
                    EntityPersister p = event.getPersister();
                    AbstractEntityPersister aep = (p instanceof AbstractEntityPersister) ? (AbstractEntityPersister) p : null;

                    if (Objects.nonNull(aep)) {
                        for (String idCol : aep.getIdentifierColumnNames()) {
                            if (Objects.nonNull(idCol) && !idCol.isEmpty()) {
                                ResultSet rs = conn.getMetaData().getColumns(null, null, his, idCol);
                                if (rs.next()) {
                                    st.execute("ALTER TABLE " + his + " DROP COLUMN " + idCol);
                                }
                            }
                        }
                    }
                    for (String key : primaryKeys.keySet()) {
                        st.addBatch("ALTER TABLE " + his + " ADD COLUMN " + key + " VARCHAR(100)");
                    }
                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN audit_id UUID PRIMARY KEY");
                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN audit_type VARCHAR(20)");
                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN audit_date TIMESTAMP");
                    st.executeBatch();
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });
    }

    protected void createHistoryTableIfNeeded(Session session, String base, String his, Map<String, Object> primaryKeys, PreDeleteEvent event) {

        final boolean[] exists = {false};

        session.doWork(conn -> {
            try {
                try (PreparedStatement ps = conn.prepareStatement("SELECT to_regclass(?)")) {
                    ps.setString(1, his);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getString(1) != null) {
                            exists[0] = true;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });

        if (exists[0]) return;

        session.doWork(conn -> {
            try {
                try (Statement st = conn.createStatement()) {
                    // copy
                    st.execute("CREATE TABLE " + his + " (LIKE " + base + " INCLUDING DEFAULTS INCLUDING GENERATED)");

                    // drop original PK
                    EntityPersister p = event.getPersister();
                    AbstractEntityPersister aep = (p instanceof AbstractEntityPersister) ? (AbstractEntityPersister) p : null;

                    if (Objects.nonNull(aep)) {
                        for (String idCol : aep.getIdentifierColumnNames()) {
                            if (Objects.nonNull(idCol) && !idCol.isEmpty()) {
                                ResultSet rs = conn.getMetaData().getColumns(null, null, his, idCol);
                                if (rs.next()) {
                                    st.execute("ALTER TABLE " + his + " DROP COLUMN " + idCol);
                                }
                            }
                        }
                    }

                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN id_his UUID PRIMARY KEY");
                    for (String key : primaryKeys.keySet()) {
                        st.addBatch("ALTER TABLE " + his + " ADD COLUMN " + key + " VARCHAR(100)");
                    }
                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN audit_type VARCHAR(20)");
                    st.addBatch("ALTER TABLE " + his + " ADD COLUMN audit_date TIMESTAMP");
                    st.executeBatch();
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });
    }

    // ---------------------------
    // ENSURE COLUMNS
    // ---------------------------
    protected void ensureColumns(Session s, String table, Map<String, Object> data, Map<String, Object> primaryKeys) {

        Set<String> existing = new HashSet<>();

        s.doWork(conn -> {
            try {
                try (ResultSet rs = conn.getMetaData().getColumns(null, null, table, null)) {
                    while (rs.next()) {
                        existing.add(rs.getString("COLUMN_NAME").toLowerCase());
                    }
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });

        s.doWork(conn -> {
            try {
                try (Statement st = conn.createStatement()) {
                    for (Map.Entry<String, Object> e : data.entrySet()) {
                        String col = e.getKey().toLowerCase();
                        if (existing.contains(col)) {
                            continue;
                        }
                        String sql = "ALTER TABLE " + table + " ADD COLUMN " + e.getKey() + " " + resolveType(e.getValue());
                        st.addBatch(sql);
                    }
                    st.executeBatch();
                }
            } catch (Exception e) {
                log.error("Error transaction history", e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });
    }

    protected String resolveType(Object value) {
        if (value == null) return "TEXT";
        if (value instanceof Boolean) return "BOOLEAN";
        if (value instanceof Integer) return "INTEGER";
        if (value instanceof Long) return "BIGINT";
        if (value instanceof UUID) return "UUID";
        if (value instanceof LocalDateTime) return "TIMESTAMP";
        return "TEXT";
    }

    // ---------------------------
    // INSERT
    // ---------------------------

    protected void insertHistory(Session s, String tableLive, String tableHis, Map<String, Object> data, String auditType) {
        insertHistory(s, tableLive, tableHis, data, null, auditType);
    }

    protected void insertHistory(Session s, String tableLive, String tableHis, Map<String, Object> data, Map<String, Object> primaryKeys, String auditType) {
        log.debug("audit type {}", auditType);
        // final Set<String> keyword = new HashSet<>(Arrays.asList("key", "value", "order", "group"));
        s.doWork(conn -> {

            try {
                List<String> cols = new ArrayList<>(data.keySet());
                // statement insert
                String colPart = String.join(",", cols);
                String paramPart = String.join(",", Collections.nCopies(cols.size(), "?"));
                String sql = "INSERT INTO " + tableHis + " (" + colPart + ") VALUES (" + paramPart + ")";

                // statement delete
                log.trace("audit insert sql: {}", sql);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int i = 1;
                    for (String c : cols) {
                        bind(ps, i++, data.get(c));
                        log.trace("index {} {}", i, data.get(c));
                    }
                    ps.executeUpdate();
                }
                if ("UPDATE".equalsIgnoreCase(auditType)) {
                    return;
                }
                if ("HARD_DELETE".equalsIgnoreCase(auditType)) {
                    log.debug("hard delete entity");
                    return;
                }
                if (Objects.isNull(primaryKeys)) {
                    return;
                }
                if (primaryKeys.isEmpty()) {
                    return;
                }

                List<String> colDelete = new ArrayList<>(primaryKeys.keySet());
                String whereClause = colDelete.stream().map(col -> col.replace("original_pk_", "") + " = ?").collect(Collectors.joining(" AND "));

                String sqlDelete = "DELETE FROM " + tableLive + " WHERE " + whereClause;
                log.info("delete sql {}", sqlDelete);
                try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                    int idx = 1;
                    for (String col : colDelete) {
                        log.trace("deleted param idx {} {}", idx, primaryKeys.get(col));
                        bind(ps, idx++, primaryKeys.get(col));
                    }
                    ps.executeUpdate();
                    log.debug("soft delete entity success");
                }
            } catch (Exception e) {
                log.error("error insert history: {}", e.getMessage(), e);
                // ❗ WAJIB claim error supaya SPRING TRANSACTION ROLLBACK
                throw new AuditHistoryListenerException();
            }
        });
    }

    private void bind(PreparedStatement ps, int idx, Object value) throws SQLException {
        log.trace("BIND [{}] = {}", idx, value);
        if (value instanceof Date) {
            ps.setTimestamp(idx, new Timestamp(((Date) value).getTime()));
        } else if (value instanceof LocalDateTime) {
            ps.setTimestamp(idx, Timestamp.valueOf((LocalDateTime) value));
        } else if (value instanceof LocalDate) {
            ps.setDate(idx, java.sql.Date.valueOf((LocalDate) value));
        } else {
            ps.setObject(idx, value);
        }
    }

    // relation

    private boolean isManyToOne(Class<?> entityClass, String propertyName) {
        Field field = findField(entityClass, propertyName);
        if (Objects.nonNull(field) && field.isAnnotationPresent(ManyToOne.class)) {
            return true;
        }

        // fallback: cek getter
        Method getter = findGetter(entityClass, propertyName);
        return Objects.nonNull(getter) && getter.isAnnotationPresent(ManyToOne.class);
    }

    private Method findGetter(Class<?> clazz, String fieldName) {
        String name = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Object extractEntityId(Object entity, SessionImplementor session) {
        if (entity == null) return null;
        EntityPersister persister = session.getEntityPersister(null, entity);

        return persister.getIdentifier(entity, session);
    }


}

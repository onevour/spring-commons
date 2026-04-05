package com.onevour.core.listeners;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.exceptions.AuditHistoryListenerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.sql.*;
import java.util.*;

@Slf4j
@Component
public class AuditTableCreator {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    List<EntityManagerFactory> factories;

    @Autowired
    LocalContainerEntityManagerFactoryBean factoryBean;

    @Autowired
    AuditUpdateListener listener;

    //    @PostConstruct
    @Transactional
    @PostConstruct
    public void registerEventUpdateDelete() {
        for (EntityManagerFactory emf : factories) {
            try {
                SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
                EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
                registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(listener);
                registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(listener);
                log.trace("success registered listener in {}", sessionFactory);
            } catch (Exception e) {
                log.warn("Cannot unwrap EMF → skip {}", emf.getClass().getName(), e);
            }
        }
        init();
    }
    
    /**
     * Dipanggil otomatis saat Spring context siap
     * TANPA tergantung Spring Boot
     */

    public void init() {


        log.debug("create table history");

        // Ambil SessionFactory Hibernate yang asli
        SessionFactoryImplementor sf = factoryBean.getNativeEntityManagerFactory().unwrap(SessionFactoryImplementor.class);

        // Loop semua entitas dari Hibernate metamodel
        sf.getMetamodel().getEntities().forEach(entity -> {

            Class<?> clazz = entity.getJavaType();
            if (clazz == null) return;

            if (!clazz.isAnnotationPresent(EntityHistory.class)) return;

            String tableName = resolveTableName(clazz);
            if (tableName == null || tableName.endsWith("_his")) return;

            String auditTable = tableName + "_his";
            log.debug("create table history: {}", auditTable);

            // Ambil Session dari SessionFactory (tidak lewat EM proxy!)
            Session session = sf.openSession();
            try {
                createAuditTableIfNotExist(session, tableName, auditTable);
            } finally {
                session.close();
            }
        });
    }

    private void createAuditTableIfNotExist(Session session, String tableName, String auditTable) {
        session.doWork(conn -> {
            try {
                // 1. check table exist
                boolean exist = false;
                try (PreparedStatement ps = conn.prepareStatement("SELECT to_regclass(?)")) {
                    ps.setString(1, auditTable);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getString(1) != null) {
                            exist = true;
                        }
                    }
                }
                if (exist) {
                    log.debug("table {} already exist", auditTable);
                    return;
                }
                // 2. create table and add audit field
                try (Statement st = conn.createStatement()) {
                    st.addBatch("CREATE TABLE IF NOT EXISTS " + auditTable + " AS TABLE " + tableName + " WITH NO DATA");
                    st.addBatch("ALTER TABLE " + auditTable + " ADD COLUMN IF NOT EXISTS audit_id UUID PRIMARY KEY");
                    st.addBatch("ALTER TABLE " + auditTable + " ADD COLUMN IF NOT EXISTS audit_type VARCHAR(20)");
                    st.addBatch("ALTER TABLE " + auditTable + " ADD COLUMN IF NOT EXISTS audit_date TIMESTAMP");
                    st.executeBatch();
                }
                // 3. copy index, skip pk
                Set<String> pks = new HashSet<>();
                List<Map<String, String>> indexRows = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT indexname, indexdef FROM pg_indexes WHERE tablename = ?")) {
                    ps.setString(1, tableName);
                    try (ResultSet rs = ps.executeQuery()) {
                        DatabaseMetaData dbMetaData = conn.getMetaData();
                        try (ResultSet pk = dbMetaData.getPrimaryKeys(null, null, tableName)) {
                            while (pk.next()) {
                                String columnName = pk.getString("COLUMN_NAME");
                                String columnPkName = pk.getString("PK_NAME");
                                pks.add(columnPkName);
                                log.trace("primary key: {}, index name: {}", columnName, columnPkName);
                            }
                        }
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        while (rs.next()) {
                            Map<String, String> indexRow = new LinkedHashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                log.trace("add key {}, value {}", metaData.getColumnName(i), rs.getObject(i));
                                indexRow.put(metaData.getColumnName(i), rs.getString(i));
                            }
                            indexRows.add(indexRow);

                        }
                    }
                }
                Set<String> indexTable = new HashSet<>();
                for (Map<String, String> row : indexRows) {
                    // loop column
                    for (Map.Entry<String, String> col : row.entrySet()) {
                        String key = col.getKey();
                        String value = col.getValue();
                        if ("indexname".equalsIgnoreCase(key) && pks.contains(value)) {
                            log.trace("skip copy pk key {}", value);
                            break;
                        }
                        if ("indexname".equalsIgnoreCase(key)) continue; // skip column index name
                        indexTable.add(col.getValue());
                    }
                }
                if (indexTable.isEmpty()) {
                    return;
                }
                try (Statement st = conn.createStatement()) {
                    for (String index : indexTable) {
                        // ambil nama index asli
                        String indexName = substringBetween(index, "INDEX ", " ON ");
                        String newIndexName = indexName + "_" + auditTable;

                        // ambil bagian setelah USING (ignore-case)
                        String afterUsing = substringAfterIgnoreCase(index, " using ");

                        // build SQL baru
                        String indexSql = "CREATE INDEX IF NOT EXISTS "
                                + newIndexName
                                + " ON " + auditTable
                                + " USING " + afterUsing;

                        log.trace("create new index → {}", indexSql);
                        st.addBatch(indexSql);
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

    public String substringAfterIgnoreCase(String text, String search) {
        String lowerText = text.toLowerCase();
        String lowerSearch = search.toLowerCase();
        int idx = lowerText.indexOf(lowerSearch);
        if (idx == -1) return text; // atau return ""
        return text.substring(idx + search.length());
    }


    // Ambil nama tabel dari @Table atau fallback nama entity snake_case
    private String resolveTableName(Class<?> clazz) {
        Table t = clazz.getAnnotation(Table.class);
        if (t != null && !t.name().isEmpty())
            return t.name();

        return toSnakeCase(clazz.getSimpleName());
    }

    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }

        int start = str.indexOf(open);
        if (start < 0) {
            return null;
        }

        int end = str.indexOf(close, start + open.length());
        if (end < 0) {
            return null;
        }

        return str.substring(start + open.length(), end);
    }
}

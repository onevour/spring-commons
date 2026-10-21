package com.onevour.core.applications.base;

import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/** Kontrak kolom audit: default, nama kolom, dan updatable=false untuk created_*. */
class BaseEntityTest {

    static class Sample extends BaseEntity {
    }

    @Test
    void deletedDefaultsToFalse() {
        Sample s = new Sample();
        assertEquals(Boolean.FALSE, s.getDeleted());
        assertNull(s.getCreatedBy());
        assertNull(s.getModifiedDate());
    }

    @Test
    void mappedSuperclassAndColumnContract() throws Exception {
        assertTrue(BaseEntity.class.isAnnotationPresent(MappedSuperclass.class));
        assertTrue(BaseEntityCreated.class.isAnnotationPresent(MappedSuperclass.class));

        Field deleted = BaseEntityCreated.class.getDeclaredField("deleted");
        assertEquals("is_deleted", deleted.getAnnotation(Column.class).name());

        Field createdBy = BaseEntityCreated.class.getDeclaredField("createdBy");
        assertFalse(createdBy.getAnnotation(Column.class).updatable());
        Field createdDate = BaseEntityCreated.class.getDeclaredField("createdDate");
        assertFalse(createdDate.getAnnotation(Column.class).updatable());
    }

    @Test
    void equalsIncludesSuperFields() {
        Sample a = new Sample();
        Sample b = new Sample();
        assertEquals(a, b);
        b.setDeleted(true);
        assertNotEquals(a, b);
    }
}

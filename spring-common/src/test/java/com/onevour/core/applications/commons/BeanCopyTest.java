package com.onevour.core.applications.commons;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class BeanCopyTest {

    public static class Child implements Serializable {
        private String value;
        public Child() { }
        public Child(String value) { this.value = value; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        @Override public boolean equals(Object o) { return o instanceof Child && Objects.equals(value, ((Child) o).value); }
        @Override public int hashCode() { return Objects.hashCode(value); }
    }

    public static class Source implements Serializable {
        private String name;
        private Integer age;
        private String secret;
        private Child child;
        public Source() { }
        public Source(String name, Integer age, String secret, Child child) {
            this.name = name; this.age = age; this.secret = secret; this.child = child;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public Child getChild() { return child; }
        public void setChild(Child child) { this.child = child; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Source)) return false;
            Source s = (Source) o;
            return Objects.equals(name, s.name) && Objects.equals(age, s.age) && Objects.equals(secret, s.secret) && Objects.equals(child, s.child);
        }
        @Override public int hashCode() { return Objects.hash(name, age, secret, child); }
    }

    public static class Target {
        private String name;
        private Integer age;
        private String secret;
        private String extra;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public String getExtra() { return extra; }
        public void setExtra(String extra) { this.extra = extra; }
    }

    @Test
    void copyByPropertyName() {
        Target t = BeanCopy.copy(new Source("john", 30, "s3cret", null), Target.class);
        assertEquals("john", t.getName());
        assertEquals(30, t.getAge());
        assertEquals("s3cret", t.getSecret());
        assertNull(t.getExtra());
    }

    @Test
    void copyWithExcludedProperties() {
        Target t = BeanCopy.copy(new Source("john", 30, "s3cret", null), Target.class, "secret");
        assertEquals("john", t.getName());
        assertNull(t.getSecret());
    }

    @Test
    void copyNullSourceThrows() {
        assertThrows(NullPointerException.class, () -> BeanCopy.copy(null, Target.class));
    }

    @Test
    void copyCollection() {
        List<Source> sources = Arrays.asList(new Source("a", 1, null, null), new Source("b", 2, null, null));
        List<Target> targets = BeanCopy.copyCollection(sources, Target.class);
        assertEquals(2, targets.size());
        assertEquals("b", targets.get(1).getName());
        assertTrue(BeanCopy.copyCollection(null, Target.class).isEmpty());
    }

    @Test
    void nullPropertyNamesListsOnlyNulls() {
        List<String> nulls = Arrays.asList(BeanCopy.getNullPropertyNames(new Source("john", null, null, null)));
        assertTrue(nulls.contains("age"));
        assertTrue(nulls.contains("secret"));
        assertTrue(nulls.contains("child"));
        assertFalse(nulls.contains("name"));
        assertFalse(nulls.contains("class"));
    }

    @Test
    void deepCopyIsIndependent() {
        Source original = new Source("john", 1, null, new Child("c"));
        Source copy = BeanCopy.copyDeep(original);
        assertEquals(original, copy);
        assertNotSame(original.getChild(), copy.getChild());
        copy.getChild().setValue("changed");
        assertEquals("c", original.getChild().getValue());
    }
}

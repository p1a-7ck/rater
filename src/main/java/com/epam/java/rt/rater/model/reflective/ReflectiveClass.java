package com.epam.java.rt.rater.model.reflective;

import java.util.HashMap;
import java.util.Map;

/**
 * rater
 */
public class ReflectiveClass {
    private final Class<?> entityClass;
    private final Class<?> builderClass;
    private final Map<String, ReflectiveField> fields;

    public ReflectiveClass(Class<?> entityClass, Class<?> builderClass) {
        this.entityClass = entityClass;
        this.builderClass = builderClass;
        this.fields = new HashMap<>();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Class<?> getBuilderClass() {
        return builderClass;
    }

    public void putField(String fieldName, ReflectiveField field) {
        this.fields.put(fieldName, field);
    }

    public ReflectiveField getField(String fieldName) {
        return this.fields.get(fieldName);
    }

    public int countFields() {
        return this.fields.size();
    }

    public void clearFields() {
        this.fields.clear();
    }
}

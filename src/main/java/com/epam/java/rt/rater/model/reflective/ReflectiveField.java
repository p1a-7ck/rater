package com.epam.java.rt.rater.model.reflective;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * rater
 */
public class ReflectiveField {
    private final Method setter;
    private final Method getter;
    private final Type type;

    public ReflectiveField(Type type, Method getter, Method setter) {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }

    public Type getType() {
        return type;
    }
}

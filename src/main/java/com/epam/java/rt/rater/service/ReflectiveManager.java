package com.epam.java.rt.rater.service;

import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.model.reflective.ReflectiveField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * rater
 */
public class ReflectiveManager {
    private static final Logger logger = LoggerFactory.getLogger(ReflectiveManager.class);
    private static ReflectiveManager INSTANCE;
    private final Map<String, ReflectiveClass> classes;

    private ReflectiveManager() {
        logger.info("ReflectiveManager instance created");
        this.classes = new HashMap<>();
    }

    public static ReflectiveManager getInstance() {
        if (ReflectiveManager.INSTANCE == null)
            ReflectiveManager.INSTANCE = new ReflectiveManager();
        return ReflectiveManager.INSTANCE;
    }

    public ReflectiveClass getReflectiveClass(String packageName, String className) {
        ReflectiveClass reflectiveClass = this.classes.get(className);
        if (reflectiveClass != null) return reflectiveClass;
        return createReflectiveClass(packageName, className);
    }

    private ReflectiveClass createReflectiveClass(String packageName, String className) {
        try {
            Class<?> classEntity = Class.forName(packageName.concat(".").concat(className));
            Class<?> classBuilder = null;
            try {
                classBuilder = Class.forName(packageName.concat(className.concat(".").concat("$Builder")));
                logger.info("Builder found for requested by name class '{}'", className);
            } catch (ClassNotFoundException exc) {
                logger.info("Requested by name class '{}' have no builder", className);
            }
            ReflectiveClass reflectiveClass = new ReflectiveClass(classEntity, classBuilder);
            this.classes.put(className, reflectiveClass);
            return reflectiveClass;
        } catch (ClassNotFoundException |
                LinkageError exc) {
            logger.error("Requested by name class '{}' not found or not returned", className, exc);
            return null;
        }
    }

    public Object createReflectiveObject(ReflectiveClass reflectiveClass) {
        try {
            if (reflectiveClass.getBuilderClass() != null) {
                return reflectiveClass.getBuilderClass().newInstance();
            } else if (reflectiveClass.getEntityClass() != null) {
                return reflectiveClass.getEntityClass().newInstance();
            }
            logger.error("There are no class defined for reflective object");
            return null;
        } catch (InstantiationException | IllegalAccessException exc) {
            logger.error("Can't create reflective object", exc);
            return null;
        }
    }

    public Object buildImmutableReflectiveObject(ReflectiveClass reflectiveClass, Object reflectiveObjectBuilder) {
        try {
            if (reflectiveClass.getBuilderClass() == null) return reflectiveObjectBuilder;
            if (reflectiveClass.getEntityClass() != null) {
                Constructor constructor = reflectiveClass.getEntityClass().getConstructor(reflectiveClass.getBuilderClass());
                Object reflectiveObject = constructor.newInstance(reflectiveObjectBuilder);
                reflectiveObjectBuilder = null;
                defineBeanFields(reflectiveClass);
                return reflectiveObject;
            }
            logger.error("There is no target class defined for reflective object");
        } catch (NoSuchMethodException |
                SecurityException |
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException exc) {
            logger.error("Constructor for class not found", exc);
        }
        return null;
    }

    public ReflectiveField getReflectiveField(ReflectiveClass reflectiveClass, String fieldName) {
        if (reflectiveClass == null) return null;
        if (reflectiveClass.countFields() == 0) defineBeanFields(reflectiveClass);
        return reflectiveClass.getField(fieldName);
    }

    public <T> T getReflectiveFieldValue(ReflectiveClass reflectiveClass, Object reflectiveObject,
                                         String fieldName)
            throws InvocationTargetException, IllegalAccessException {
        ReflectiveField reflectiveField = getReflectiveField(reflectiveClass, fieldName);
        if (reflectiveField == null)
            throw new IllegalStateException("Field '" + fieldName + "' not found");
        if (reflectiveField.getGetter() == null)
            throw new IllegalStateException("Getter for field '" + fieldName + "' not found");
        return (T) reflectiveField.getGetter().invoke(reflectiveObject);
    }

    public <T> void setReflectiveFieldValue(ReflectiveClass reflectiveClass, Object reflectiveObject,
                                            String fieldName, T fieldValue)
            throws InvocationTargetException, IllegalAccessException {
        ReflectiveField reflectiveField = getReflectiveField(reflectiveClass, fieldName);
        if (reflectiveField == null)
            throw new IllegalStateException("Field '" + fieldName + "' not found");
        if (reflectiveField.getSetter() == null)
            throw new IllegalStateException("Setter for field '" + fieldName + "' not found");
        reflectiveField.getSetter().invoke(reflectiveObject, fieldValue);
    }

    private void defineBeanFields(ReflectiveClass reflectiveClass) {
        ReflectiveField reflectiveField;
        String fieldName;
        Class<?> beanClass = null;
        reflectiveClass.clearFields();
        if (reflectiveClass.getBuilderClass() != null) {
            beanClass = reflectiveClass.getBuilderClass();
        } else if (reflectiveClass.getEntityClass() != null) {
            beanClass = reflectiveClass.getEntityClass();
        }
        if (beanClass != null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    fieldName = propertyDescriptor.getName().substring(0, 1).toUpperCase()
                            .concat(propertyDescriptor.getName().substring(1));
                    if (!"Class".equals(fieldName)) {
                        try {
                            reflectiveField = new ReflectiveField(
                                    propertyDescriptor.getPropertyType(),
                                    beanClass.getMethod("get".concat(fieldName)),
                                    beanClass.getMethod("set".concat(fieldName), propertyDescriptor.getPropertyType()));
                            reflectiveClass.putField(propertyDescriptor.getName(), reflectiveField);
                        } catch (NoSuchMethodException exc) {
                            logger.error("Getter or setter not found", exc);
                        }
                    }
                }
            } catch (IntrospectionException exc) {
                logger.error("Getting bean info error", exc);
            }
        }
    }

    public void setFieldValueObject(Object reflectiveObject, Method setterMethod, Object valueObject) {
        Class<?>[] parameterTypes = setterMethod.getParameterTypes();
        Class c = parameterTypes[0].getClass();
        try {
            setterMethod.invoke(reflectiveObject, c.cast(valueObject));
        } catch (IllegalAccessException |
                InvocationTargetException exc) {
            logger.error("Method invoking error", exc);
        }
    }
}

package com.epam.java.rt.rater.service;

import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.model.reflective.ReflectiveField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
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
                classBuilder = Class.forName(packageName.concat(".").concat(className).concat("$Builder"));
                logger.info("Builder found for requested by name class '{}'", className);
            } catch (ClassNotFoundException exc) {
                logger.info("Requested by name class '{}' have no builder", className);
            }
            ReflectiveClass reflectiveClass = new ReflectiveClass(classEntity, classBuilder);
            this.classes.put(className, reflectiveClass);
            return reflectiveClass;
        } catch (ClassNotFoundException |
                LinkageError exc) {
            logger.error("Requested by name class '{}' not found or not returned ({})", className, exc.getMessage());
            return null;
        }
    }

    private Object newOrGetInstance(Class<?> sourceClass)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return sourceClass.newInstance();
        } catch (IllegalAccessException exc) {
            Method method = ReflectiveManager.getInstance().getClassMethod(sourceClass, "getInstance");
            if (method == null) throw new InstantiationException();
            return method.invoke(null);
        }
    }

    public Object createReflectiveObject(ReflectiveClass reflectiveClass) {
        try {
            if (reflectiveClass.getBuilderClass() != null) {
                return newOrGetInstance(reflectiveClass.getBuilderClass());
            } else if (reflectiveClass.getEntityClass() != null) {
                return newOrGetInstance(reflectiveClass.getEntityClass());
            }
            logger.error("There are no class defined for reflective object");
            return null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exc) {
            logger.error("Can't create reflective object", exc);
            return null;
        }
    }

    public Object buildImmutableReflectiveObject(ReflectiveClass reflectiveClass, Object reflectiveObjectBuilder) {
        try {
            String bName = "", eName = "";
            if (reflectiveClass.getBuilderClass() != null) bName = reflectiveClass.getBuilderClass().getName();
            if (reflectiveClass.getEntityClass() != null) eName = reflectiveClass.getEntityClass().getName();

            System.out.println("reflectiveClass = " + reflectiveClass.getClass().getName() +
                    ", reflectiveClass.getBuilderClass() = " + bName + ", reflectiveClass.getEntityClass() = " + eName);
            if (reflectiveClass.getBuilderClass() == null) return reflectiveObjectBuilder;
            if (reflectiveClass.getEntityClass() != null) {
                Constructor constructor = reflectiveClass.getEntityClass().getConstructor(reflectiveClass.getBuilderClass());
                System.out.println(reflectiveObjectBuilder);
                Object reflectiveObject = constructor.newInstance(reflectiveObjectBuilder);
                reflectiveObjectBuilder = null;
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
        if (reflectiveClass.countFields() == 0) defineReflectiveClassFields(reflectiveClass);
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

    private void defineReflectiveClassFields(ReflectiveClass reflectiveClass) {
        logger.debug("defineReflectiveClassFields({})", reflectiveClass);
        ReflectiveField reflectiveField = null;
        String fieldName;
        Class<?> fieldHolderClass = null;
        reflectiveClass.clearFields();
        if (reflectiveClass.getBuilderClass() != null) {
            fieldHolderClass = reflectiveClass.getBuilderClass();
        } else if (reflectiveClass.getEntityClass() != null) {
            fieldHolderClass = reflectiveClass.getEntityClass();
        }
        if (fieldHolderClass != null) {
            for (Field field : fieldHolderClass.getDeclaredFields()) {
                fieldName = field.getName().substring(0, 1).toUpperCase().concat(field.getName().substring(1));
                if (List.class.isAssignableFrom(field.getType())) {
                    fieldName = fieldName.substring(0, fieldName.length() - 4); // ...List - field
                    reflectiveField = new ReflectiveField(
                            field.getType(),
                            getClassMethod(fieldHolderClass, "get".concat(fieldName), int.class),
                            getClassMethod(fieldHolderClass, "add".concat(fieldName),
                                    (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    fieldName = fieldName.substring(0, fieldName.length() - 3); // ...Map - field
                    reflectiveField = new ReflectiveField(
                            field.getType(),
                            getClassMethod(fieldHolderClass, "get".concat(fieldName),
                                    (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]),
                            getClassMethod(fieldHolderClass, "put".concat(fieldName),
                                    (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0],
                                    (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1]));
                } else {
                    reflectiveField = new ReflectiveField(
                            field.getType(),
                            getClassMethod(fieldHolderClass, "get".concat(fieldName)),
                            getClassMethod(fieldHolderClass, "set".concat(fieldName), field.getType()));
                }
                reflectiveClass.putField(field.getName(), reflectiveField);
            }
        }
    }

    private Method getClassMethod(Class<?> holderClass, String methodName) {
        try {
            return holderClass.getMethod(methodName);
        } catch (NoSuchMethodException exc) {
            logger.error("Class '{}' have no method named '{}' with no parameter",
                    holderClass.getName(), methodName);
        }
        return null;
    }

    private Method getClassMethod(Class<?> holderClass, String methodName,
                                  Class<?> parameterType) {
        try {
            return holderClass.getMethod(methodName, parameterType);
        } catch (NoSuchMethodException exc) {
            logger.error("Class '{}' have no method named '{}' with parameter type '{}'",
                    holderClass.getName(), methodName, parameterType.getName());
        }
        return null;
    }

    private Method getClassMethod(Class<?> holderClass, String methodName,
                                  Class<?> parameterType1, Class<?> parameterType2) {
        try {
            return holderClass.getMethod(methodName, parameterType1, parameterType2);
        } catch (NoSuchMethodException exc) {
            logger.error("Class '{}' have no method named '{}' with parameters types '{}, {}'",
                    holderClass.getName(), methodName, parameterType1.getName(), parameterType2.getName());
        }
        return null;
    }

}
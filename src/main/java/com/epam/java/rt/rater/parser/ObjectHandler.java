package com.epam.java.rt.rater.parser;

import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.model.reflective.ReflectiveField;
import com.epam.java.rt.rater.model.reflective.ReflectiveStack;
import com.epam.java.rt.rater.service.FromStringAdapter;
import com.epam.java.rt.rater.service.ReflectiveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * rater
 */
class ObjectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObjectHandler.class);
    private static final ReflectiveStack<ObjectHandler> objectHandlerStack = new ReflectiveStack<>();
    private static String packageName;

    private Class<?> currentElementClass;
    private ReflectiveClass parentReflectiveClass;
    private Object parentReflectiveObject;
    private ReflectiveClass currentReflectiveClass;
    private Object currentReflectiveObject;

    private ReflectiveField currentReflectiveField;
    private Object elementValue;

    static void setPackageName(String packageName) {
        ObjectHandler.packageName = packageName;
    }

    static ObjectHandler newObjectHandler() {
        ObjectHandler.objectHandlerStack.push(new ObjectHandler(null, null));
        return ObjectHandler.objectHandlerStack.getCurrent();
    }

    static Object closeObjectHandler() {
        return ObjectHandler.objectHandlerStack.pop().currentReflectiveObject;
    }

    ObjectHandler getCurrent() {
        return ObjectHandler.objectHandlerStack.getCurrent();
    }

    ObjectHandler(ReflectiveClass parentReflectiveClass, Object parentReflectiveObject) {
        this.parentReflectiveClass = parentReflectiveClass;
        this.parentReflectiveObject = parentReflectiveObject;
    }

    private Class<?> defineElementClass(String elementName) {
        if (this.parentReflectiveClass != null) {
            this.currentReflectiveField = ReflectiveManager.getInstance()
                    .getReflectiveField(this.parentReflectiveClass, elementName);
            if (this.currentReflectiveField != null) {
                this.elementValue = new StringBuilder();
                return this.currentReflectiveField.getClass();
            }
        }
        this.currentReflectiveClass = ReflectiveManager.getInstance()
                .getReflectiveClass(ObjectHandler.packageName,
                        elementName.substring(0, 1).toUpperCase().concat(elementName.substring(1)));
        if (this.currentReflectiveClass != null) {
            this.currentReflectiveObject = ReflectiveManager.getInstance()
                    .createReflectiveObject(this.currentReflectiveClass);
            return this.currentReflectiveClass.getClass();
        }
        return null;
    }

    void startElement(String elementName) {
        logger.debug("startElement({})", elementName);
        if (this.currentElementClass == null) {
            this.currentElementClass = defineElementClass(elementName);
        } else {
            if (this.currentElementClass == ReflectiveClass.class) {
                ObjectHandler.objectHandlerStack
                        .push(new ObjectHandler(this.currentReflectiveClass, this.currentReflectiveObject));
                ObjectHandler.objectHandlerStack.getCurrent().startElement(elementName);
            } else if (this.currentElementClass == ReflectiveField.class) {
                ObjectHandler.objectHandlerStack
                        .push(new ObjectHandler(null, null));
                ObjectHandler.objectHandlerStack.getCurrent().startElement(elementName);
            }
        }
    }

    void elementContent(String contentValue) {
        if (this.currentElementClass == ReflectiveField.class
                && this.currentReflectiveField != null && this.elementValue instanceof StringBuilder)
            ((StringBuilder) this.elementValue).append(contentValue);
    }

    private void setElementValue(ObjectHandler objectHandler) {
        if (objectHandler.currentReflectiveField != null) {
            try {
                if (objectHandler.parentReflectiveObject != null) {
                    if (objectHandler.elementValue instanceof StringBuilder) {
                        objectHandler.currentReflectiveField.getSetter().invoke(objectHandler.parentReflectiveObject,
                                FromStringAdapter.convert(objectHandler.currentReflectiveField.getType(),
                                        objectHandler.elementValue.toString()));
                    } else {
                        objectHandler.currentReflectiveField.getSetter().invoke(objectHandler.parentReflectiveObject,
                                objectHandler.elementValue);
                    }
                    objectHandler.elementValue = null;
                }
            } catch (InvocationTargetException | IllegalAccessException exc) {
                logger.error("Field '{}' not set ({})", objectHandler.currentReflectiveField.getType().getName(),
                        exc.getMessage());
            }
        }
    }

    void endElement(String elementName) {
        logger.debug("endElement({})", elementName);
        if (this.currentElementClass == ReflectiveField.class) {
            setElementValue(this);
            ObjectHandler.objectHandlerStack.pop();
        } else if (this.currentElementClass == ReflectiveClass.class && this.currentReflectiveClass != null) {
            if (ObjectHandler.objectHandlerStack.getSize() > 1) {
                ObjectHandler.objectHandlerStack.pop();
                ObjectHandler parent = ObjectHandler.objectHandlerStack.getCurrent();
                if (parent.currentElementClass == ReflectiveField.class) {
                    parent.elementValue = ReflectiveManager.getInstance()
                            .buildImmutableReflectiveObject(this.currentReflectiveClass, this.currentReflectiveObject);
                    setElementValue(parent);
                } else if (parent.currentElementClass == ReflectiveClass.class) {
                    setElementValue(parent);
                }
            } else if (ObjectHandler.objectHandlerStack.getSize() == 1) {
                this.currentReflectiveObject = ReflectiveManager.getInstance()
                        .buildImmutableReflectiveObject(this.currentReflectiveClass, this.currentReflectiveObject);
            }
        }
    }
}

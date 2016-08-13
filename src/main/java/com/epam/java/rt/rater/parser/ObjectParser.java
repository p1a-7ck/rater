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
public class ObjectParser {
    private static final Logger logger = LoggerFactory.getLogger(ObjectParser.class);
    private static final ReflectiveStack<ObjectHandler> objectHandlerStack = new ReflectiveStack<>();
    private String packageName;

    private ObjectParser(Package modelPackage) {
        this.packageName = modelPackage.getName();
    }

    public ObjectHandler getObjectHandler() {
        return ObjectParser.objectHandlerStack.getCurrent();
    }

    public static ObjectParser to(Package modelPackage) {
        return new ObjectParser(modelPackage);
    }

    public Object parseXML(Class parserClass, String fileName) {
        ObjectParser.objectHandlerStack.push(this.new ObjectHandler(null, null));
        if (parserClass == SAXParser.class) {
            logger.info("Parsing with SAXParser initiated");
            new SAXParser(this, ObjectParser.class.getClassLoader().getResourceAsStream(fileName));
        } else if (parserClass == StAXParser.class) {
            logger.info("Parsing with StAXParser initiated");
            new StAXParser(this, ObjectParser.class.getClassLoader().getResourceAsStream(fileName));
        } else if (parserClass == DOMParser.class) {
            logger.info("Parsing with DOMParser initiated");
            new DOMParser(this, ObjectParser.class.getClassLoader().getResourceAsStream(fileName));
        }
        Object result = ObjectParser.objectHandlerStack.getCurrent().currentReflectiveObject;
        ObjectParser.objectHandlerStack.pop();
        return result;
    }

    class ObjectHandler {
        private Class<?> currentElementClass;
        private ReflectiveClass parentReflectiveClass;
        private Object parentReflectiveObject;
        private ReflectiveClass currentReflectiveClass;
        private Object currentReflectiveObject;

        private ReflectiveField currentReflectiveField;
        private Object elementValue;

        public ObjectHandler(ReflectiveClass parentReflectiveClass, Object parentReflectiveObject) {
            this.parentReflectiveClass = parentReflectiveClass;
            this.parentReflectiveObject = parentReflectiveObject;
        }

        public Object getResult() {
            return null;
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
                    .getReflectiveClass(ObjectParser.this.packageName,
                            elementName.substring(0, 1).toUpperCase().concat(elementName.substring(1)));
            if (this.currentReflectiveClass != null) {
                this.currentReflectiveObject = ReflectiveManager.getInstance()
                        .createReflectiveObject(this.currentReflectiveClass);
                return this.currentReflectiveClass.getClass();
            }
            return null;
        }

        public void startElement(String elementName) {
            logger.debug("startElement({})", elementName);
            if (this.currentElementClass == null) {
                this.currentElementClass = defineElementClass(elementName);
            } else {
                if (this.currentElementClass == ReflectiveClass.class) {
                    ObjectParser.objectHandlerStack.push(ObjectParser.this
                            .new ObjectHandler(this.currentReflectiveClass, this.currentReflectiveObject));
                    ObjectParser.objectHandlerStack.getCurrent().startElement(elementName);
                } else if (this.currentElementClass == ReflectiveField.class) {
                    ObjectParser.objectHandlerStack.push(ObjectParser.this
                            .new ObjectHandler(null, null));
                    ObjectParser.objectHandlerStack.getCurrent().startElement(elementName);
                }
            }
        }

        public void elementContent(String contentValue) {
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

        public void endElement(String elementName) {
            logger.debug("endElement({})", elementName);
            if (this.currentElementClass == ReflectiveField.class) {
                setElementValue(this);
                ObjectParser.objectHandlerStack.pop();
            } else if (this.currentElementClass == ReflectiveClass.class && this.currentReflectiveClass != null) {
                if (ObjectParser.objectHandlerStack.getSize() > 1) {
                    ObjectParser.objectHandlerStack.pop();
                    ObjectHandler parent = ObjectParser.objectHandlerStack.getCurrent();
                    if (parent.currentElementClass == ReflectiveField.class) {
                        parent.elementValue = ReflectiveManager.getInstance()
                                .buildImmutableReflectiveObject(this.currentReflectiveClass, this.currentReflectiveObject);
                        setElementValue(parent);
                    } else if (parent.currentElementClass == ReflectiveClass.class) {
                        setElementValue(parent);
                    }
                } else if (ObjectParser.objectHandlerStack.getSize() == 1) {
                    this.currentReflectiveObject = ReflectiveManager.getInstance()
                            .buildImmutableReflectiveObject(this.currentReflectiveClass, this.currentReflectiveObject);
                }
            }
        }
    }
}

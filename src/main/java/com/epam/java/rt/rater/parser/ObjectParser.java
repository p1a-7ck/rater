package com.epam.java.rt.rater.parser;

import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.model.reflective.ReflectiveField;
import com.epam.java.rt.rater.model.reflective.ReflectiveStack;
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
    private ObjectHandler objectHandler;

    private ObjectParser(Package modelPackage) {
        this.packageName = modelPackage.getName();
    }

    public ObjectHandler getObjectHandler() {
        return this.objectHandler;
    }

    public static ObjectParser to(Package modelPackage) {
        return new ObjectParser(modelPackage);
    }

    public Object parseXML(Class parserClass, String fileName) {
        this.objectHandler = this.new ObjectHandler(null, null);
        if (parserClass == SAXParser.class) {
            logger.info("Parsing with SAXParser initiated");
            new SAXParser(this, ObjectParser.class.getClassLoader().getResourceAsStream(fileName));
        }
        return objectHandler.getResult();
    }

    class ObjectHandler {
        private ReflectiveClass parentReflectiveClass;
        private Object parentReflectiveObject;
        private ReflectiveClass currentReflectiveClass;
        private ReflectiveField currentReflectiveField;
        private StringBuilder contentValue;
        private Object result;

        public ObjectHandler(ReflectiveClass parentReflectiveClass, Object parentReflectiveObject) {
            this.parentReflectiveClass = parentReflectiveClass;
            this.parentReflectiveObject = parentReflectiveObject;
        }

        public Object getResult() {
            return this.result;
        }

        public void startElement(String elementName) {
            this.currentReflectiveClass = ReflectiveManager.getInstance()
                    .getReflectiveClass(ObjectParser.this.packageName,
                            elementName.substring(0, 1).toUpperCase().concat(elementName.substring(1)));
            if (this.currentReflectiveClass == null) {
                this.currentReflectiveField = ReflectiveManager.getInstance()
                        .getReflectiveField(this.parentReflectiveClass, elementName);
                if (this.currentReflectiveField == null)
                    throw new IllegalStateException("Field '" + elementName + "' for Class '" +
                            this.currentReflectiveClass.getEntityClass().getName() + "' not found");
                this.contentValue = new StringBuilder();
            } else {
                this.result = ReflectiveManager.getInstance().createReflectiveObject(this.currentReflectiveClass);
                ObjectParser.objectHandlerStack.push(this);
                ObjectParser.this.objectHandler = new ObjectHandler(this.currentReflectiveClass, this.result);
            }
        }

        public void elementContent(String contentValue) {
            if (this.currentReflectiveField != null && this.contentValue != null)
                this.contentValue.append(contentValue);
        }

        public void endElement(String elementName) {
            if (this.currentReflectiveField != null) {
                try {
                    ReflectiveManager.getInstance()
                            .setReflectiveFieldValue(this.parentReflectiveClass, this.parentReflectiveObject,
                                    elementName, this.contentValue);
                } catch (InvocationTargetException | IllegalAccessException exc) {
                    logger.error("Field '{}' not set", elementName, exc);
                }
            } else if (this.currentReflectiveClass != null && this.result != null) {
                this.result = ReflectiveManager.getInstance()
                        .buildImmutableReflectiveObject(this.currentReflectiveClass, this.result);
            }
        }
    }
}

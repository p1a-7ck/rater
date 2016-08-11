package com.epam.java.rt.rater.parser;

import com.epam.java.rt.rater.model.Tariffs;
import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.model.reflective.ReflectiveField;
import com.epam.java.rt.rater.service.ReflectiveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * rater
 */
public class ObjectParser {
    private static final Logger logger = LoggerFactory.getLogger(ObjectParser.class);
    private Object parsedObject;
    private InputStream inputStream;
    private List<ParseLevel> parseLevelStack;
    private ParseLevel currentParseLevel;
    private boolean forceEndElement;

    public static Object getParsedObject(Class parserClass, String fileName) {
        ObjectParser objectParser = new ObjectParser();
        objectParser.parsedObject = new ArrayList<>();
        objectParser.inputStream = ObjectParser.class.getClassLoader().getResourceAsStream(fileName);
        objectParser.parseLevelStack = new LinkedList<>();
        objectParser.currentParseLevel = null;
        objectParser.forceEndElement = false;
        if (parserClass == SAXParser.class) {
            new SAXParser(objectParser);
        }
        return objectParser.parsedObject;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void startDocument() {
        logger.info("XML document parse process start");
    }

    public void startElement(String elementName) {
        logger.debug("startElement '{}'", elementName);
        if (this.currentParseLevel != null) {
            if (!createObjectField(elementName)) {
                //subEntity

            }
        } else {
            ReflectiveClass reflectiveClass = ReflectiveManager.getInstance()
                    .getReflectiveClass(Tariffs.class.getPackage().getName(),
                            elementName.substring(0, 1).toUpperCase().concat(elementName.substring(1)));
            if (reflectiveClass == null) {
                //
                this.forceEndElement = true;
            } else {
                createObject(reflectiveClass);
                this.parseLevelStack.add(this.currentParseLevel);
                logger.debug("this.parseLevelStack.add({});", this.currentParseLevel);
            }
        }
    }

    public void elementContent(String contentValue) {
        logger.debug("elementContent '{}'", contentValue);
        if (this.currentParseLevel != null) {
            if (this.currentParseLevel.reflectiveField != null &&
                    this.currentParseLevel.reflectiveFieldValue instanceof StringBuilder) {
                ((StringBuilder) this.currentParseLevel.reflectiveFieldValue).append(contentValue);
            }
        }
    }

    public void endElement(String elementName) {
        logger.debug("endElement '{}'", elementName);
        if (!this.forceEndElement && this.currentParseLevel != null) {
            if (this.currentParseLevel.reflectiveField != null) {
                fillObjectField(elementName);
            } else if (this.currentParseLevel.reflectiveObject != null) {
                Object object = constructObject();
                if (this.parseLevelStack.size() > 0) {
                    this.currentParseLevel = this.parseLevelStack.get(this.parseLevelStack.size() - 1);
                    this.parseLevelStack.remove(this.parseLevelStack.size() - 1);
                    System.out.println(this.currentParseLevel);
                    this.currentParseLevel.reflectiveFieldValue = object;
                } if (this.parseLevelStack.size() == 0) {
                    this.parsedObject = object;
                }
            }
        }
        this.forceEndElement = false;
    }

    public void endDocument() {
        logger.info("XML document parse process end");
    }

    private void createObject(ReflectiveClass reflectiveClass) {
        if (reflectiveClass != null) {
            Object reflectiveObject = ReflectiveManager.getInstance()
                    .createReflectiveObject(reflectiveClass);
            if (reflectiveObject != null) {
                this.currentParseLevel = new ParseLevel();
                this.currentParseLevel.reflectiveClass = reflectiveClass;
                this.currentParseLevel.reflectiveObject = reflectiveObject;
            } else {
                this.forceEndElement = true;
            }
        } else {
            this.forceEndElement = true;
        }
    }

    private boolean createObjectField(String fieldName) {
        this.currentParseLevel.reflectiveField = ReflectiveManager.getInstance()
                .getReflectiveField(this.currentParseLevel.reflectiveClass, fieldName);
        if (this.currentParseLevel.reflectiveField == null) {
            forceEndElement = true;
            this.currentParseLevel.reflectiveFieldValue = null;
            return false;
        }
        this.currentParseLevel.reflectiveFieldValue = new StringBuilder();
        return true;
    }

    private void fillObjectField(String fieldName) {
        if (this.currentParseLevel != null) {
            if (this.currentParseLevel.reflectiveField != null) {
                try {
                    if (this.currentParseLevel.reflectiveFieldValue instanceof StringBuilder) {
                        ReflectiveManager.getInstance().setReflectiveFieldValue
                                (this.currentParseLevel.reflectiveClass, this.currentParseLevel.reflectiveObject,
                                        fieldName, this.currentParseLevel.reflectiveFieldValue.toString());
                    } else {

                    }
                } catch (InvocationTargetException | IllegalAccessException exc) {
                    logger.error("Field '{}' value error", fieldName, exc);
                }
            }
        }
    }

    private Object constructObject() {
        if (this.currentParseLevel != null) {
            if (this.currentParseLevel.reflectiveObject != null) {
                return ReflectiveManager.getInstance().buildImmutableReflectiveObject
                        (this.currentParseLevel.reflectiveClass, this.currentParseLevel.reflectiveObject);
            }
        }
        return null;
    }

    private static class ParseLevel {
        ReflectiveClass reflectiveClass;
        Object reflectiveObject;
        ReflectiveField reflectiveField;
        Object reflectiveFieldValue;
    }
}

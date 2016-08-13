package com.epam.java.rt.rater.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rater
 */
public class ObjectParser {
    private static final Logger logger = LoggerFactory.getLogger(ObjectParser.class);

    private ObjectParser(Package modelPackage) {
        ObjectHandler.setPackageName(modelPackage.getName());
    }

    public static ObjectParser to(Package modelPackage) {
        return new ObjectParser(modelPackage);
    }

    public Object parseXML(Class<?> parserClass, String fileName) {
        try {
            logger.info("Parsing with '{}' initiating", parserClass.getSimpleName());
            ((Parser) parserClass.newInstance()).parse(ObjectHandler.newObjectHandler(),
                    ObjectParser.class.getClassLoader().getResourceAsStream(fileName));
            logger.info("Parsing with '{}' complete", parserClass.getSimpleName());
            return ObjectHandler.closeObjectHandler();
        } catch (InstantiationException | IllegalAccessException exc) {
            logger.error("Parser initiating error");
        }
        return null;
    }
}

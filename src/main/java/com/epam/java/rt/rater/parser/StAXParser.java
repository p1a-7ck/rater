package com.epam.java.rt.rater.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * rater
 */
public class StAXParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(StAXParser.class);

    StAXParser() {
    }

    @Override
    public void parse(ObjectHandler objectHandler, InputStream inputStream) {
        int chType;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
            while (streamReader.hasNext()) {
                chType = streamReader.next();
                if (chType == XMLStreamConstants.START_ELEMENT) {
                    objectHandler.getCurrent().startElement(streamReader.getLocalName());
                    for (int i = 0; i < streamReader.getAttributeCount(); i++) {
                        objectHandler.getCurrent().startElement(streamReader.getAttributeLocalName(i));
                        objectHandler.getCurrent().elementContent(streamReader.getAttributeValue(i));
                        objectHandler.getCurrent().endElement(streamReader.getAttributeLocalName(i));
                    }
                } else if (chType == XMLStreamConstants.CHARACTERS) {
                    objectHandler.getCurrent().elementContent(streamReader.getText());
                } else if (chType == XMLStreamConstants.END_ELEMENT) {
                    objectHandler.getCurrent().endElement(streamReader.getLocalName());
                }
            }
        } catch (XMLStreamException exc) {
            logger.error("Parsing error:\n", exc);
        }
    }
}

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
public class StAXParser {
    private static final Logger logger = LoggerFactory.getLogger(StAXParser.class);

    StAXParser(ObjectParser objectParser, InputStream inputStream) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            StAXHandler(objectParser, inputFactory.createXMLStreamReader(inputStream));
        } catch (XMLStreamException exc) {
            logger.error("Parsing error:\n", exc);
        }

    }

    private void StAXHandler(ObjectParser objectParser, XMLStreamReader streamReader) {
        int chType;
        try {
            while (streamReader.hasNext()) {
                chType = streamReader.next();
                if (chType == XMLStreamConstants.START_ELEMENT) {
                    objectParser.getObjectHandler().startElement(streamReader.getLocalName());
                    for (int i = 0; i < streamReader.getAttributeCount(); i++) {
                        objectParser.getObjectHandler().startElement(streamReader.getAttributeLocalName(i));
                        objectParser.getObjectHandler().elementContent(streamReader.getAttributeValue(i));
                        objectParser.getObjectHandler().endElement(streamReader.getAttributeLocalName(i));
                    }
                } else if (chType == XMLStreamConstants.CHARACTERS) {
                    objectParser.getObjectHandler().elementContent(streamReader.getText());
                } else if (chType == XMLStreamConstants.END_ELEMENT) {
                    objectParser.getObjectHandler().endElement(streamReader.getLocalName());
                }
            }
        } catch (XMLStreamException exc) {
            logger.error("Parsing error:\n", exc);
        }
    }
}

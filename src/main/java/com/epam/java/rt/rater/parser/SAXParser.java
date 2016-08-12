package com.epam.java.rt.rater.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * rater
 */
public class SAXParser {
    private static final Logger logger = LoggerFactory.getLogger(SAXParser.class);

    SAXParser(ObjectParser objectParser, InputStream inputStream) {
        try {
            SAXHandler handler = new SAXHandler(objectParser);
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(inputStream));
        } catch (SAXException exc) {
            logger.error("SAX XMLReader creating error", exc);
        } catch (IOException exc) {
            logger.error("SAX IO source error", exc);
        }
    }

    private class SAXHandler extends DefaultHandler {
        private ObjectParser objectParser;

        SAXHandler(ObjectParser objectParser) {
            this.objectParser = objectParser;
        }

        public void startDocument() {}

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.objectParser.getObjectHandler().startElement(localName);
            for(int i = 0; i < attributes.getLength(); i++) {
                this.objectParser.getObjectHandler().startElement(attributes.getLocalName(i));
                this.objectParser.getObjectHandler().elementContent(attributes.getValue(i));
                this.objectParser.getObjectHandler().endElement(attributes.getLocalName(i));
            }
        }

        public void characters(char[] ch, int start, int length) {
            this.objectParser.getObjectHandler().elementContent((new String(ch, start, length)).trim());
        }

        public void endElement(String uri, String localName, String qName) {
            this.objectParser.getObjectHandler().endElement(localName);
        }

        public void endDocument() {}
    }
}


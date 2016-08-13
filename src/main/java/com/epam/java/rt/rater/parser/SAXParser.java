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
public class SAXParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(SAXParser.class);

    SAXParser() {
    }

    @Override
    public void parse(ObjectHandler objectHandler, InputStream inputStream) {
        try {
            SAXHandler handler = new SAXHandler(objectHandler);
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
        private ObjectHandler objectHandler;

        SAXHandler(ObjectHandler objectHandler) {
            this.objectHandler = objectHandler;
        }

        @Override
        public void startDocument() {}

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.objectHandler.getCurrent().startElement(localName);
            for(int i = 0; i < attributes.getLength(); i++) {
                this.objectHandler.getCurrent().startElement(attributes.getLocalName(i));
                this.objectHandler.getCurrent().elementContent(attributes.getValue(i));
                this.objectHandler.getCurrent().endElement(attributes.getLocalName(i));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            this.objectHandler.getCurrent().elementContent((new String(ch, start, length)).trim());
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            this.objectHandler.getCurrent().endElement(localName);
        }

        @Override
        public void endDocument() {}
    }
}


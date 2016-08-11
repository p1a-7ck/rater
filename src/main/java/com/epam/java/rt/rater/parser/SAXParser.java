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
    private SAXHandler handler;
    private XMLReader reader;

    public SAXParser(ObjectParser objectParser) {
        this.handler = new SAXHandler(objectParser);
        try {
            reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(this.handler);
            InputSource inputSource = new InputSource(objectParser.getInputStream());
            reader.parse(inputSource);
        } catch (SAXException exc) {
            logger.error("SAX XMLReader creating error", exc);
        } catch (IOException exc) {
            logger.error("SAX IO source error", exc);
        }
    }

    class SAXHandler extends DefaultHandler {
        private ObjectParser objectParser;

        public SAXHandler(ObjectParser objectParser) {
            this.objectParser = objectParser;
        }

        public void startDocument() {
            this.objectParser.startDocument();
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.objectParser.startElement(localName);
            for(int i = 0; i < attributes.getLength(); i++) {
                this.objectParser.startElement(attributes.getLocalName(i));
                this.objectParser.elementContent(attributes.getValue(i));
                this.objectParser.endElement(attributes.getLocalName(i));
            }
        }

        public void characters(char[] ch, int start, int length) {
            this.objectParser.elementContent((new String(ch, start, length)).trim());
        }

        public void endElement(String uri, String localName, String qName) {
            this.objectParser.endElement(localName);
        }

        public void endDocument() {
            this.objectParser.endDocument();
        }
    }
}


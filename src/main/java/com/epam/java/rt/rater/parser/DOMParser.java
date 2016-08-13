package com.epam.java.rt.rater.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * rater
 */
public class DOMParser implements Parser {
    private static final Logger logger = LoggerFactory.getLogger(DOMParser.class);

    DOMParser() {
    }

    @Override
    public void parse(ObjectHandler objectHandler, InputStream inputStream) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            parseNode(objectHandler, document.getFirstChild());
        } catch (ParserConfigurationException | IOException | SAXException exc) {
            logger.error("Parsing error\n", exc);
        }
    }

    private void parseNode(ObjectHandler objectHandler, Node node) {
        objectHandler.getCurrent().startElement(node.getNodeName());
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                objectHandler.getCurrent().startElement(attributes.item(i).getNodeName());
                objectHandler.getCurrent().elementContent(attributes.item(i).getTextContent());
                objectHandler.getCurrent().endElement(attributes.item(i).getNodeName());
            }
        }
        objectHandler.getCurrent().elementContent(node.getTextContent());
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                parseNode(objectHandler.getCurrent(), children.item(i));
        objectHandler.getCurrent().endElement(node.getNodeName());
    }
}

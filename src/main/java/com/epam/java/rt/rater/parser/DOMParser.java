package com.epam.java.rt.rater.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * rater
 */
public class DOMParser {
    private static final Logger logger = LoggerFactory.getLogger(DOMParser.class);

    DOMParser(ObjectParser objectParser, InputStream inputStream) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            DOMHandler(objectParser, document.getFirstChild());
        } catch (ParserConfigurationException | IOException | SAXException exc) {
            logger.error("Parsing error\n", exc);
        }
    }

    private void DOMHandler(ObjectParser objectParser, Node node) {
        objectParser.getObjectHandler().startElement(node.getNodeName());
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                objectParser.getObjectHandler().startElement(attributes.item(i).getNodeName());
                objectParser.getObjectHandler().elementContent(attributes.item(i).getTextContent());
                objectParser.getObjectHandler().endElement(attributes.item(i).getNodeName());
            }
        }
        objectParser.getObjectHandler().elementContent(node.getTextContent());
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) DOMHandler(objectParser, children.item(i));
        objectParser.getObjectHandler().endElement(node.getNodeName());
    }

}

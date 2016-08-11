package com.epam.java.rt.rater.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * rater
 */
public class SAXHandler extends DefaultHandler {
    private final List<Object> objects;
    //    private ReflectiveClass reflectiveClass;
    private Object currentObject;
    private boolean resultReady = false;

    public SAXHandler() {
        this.objects = new ArrayList<>();
    }

    public List<Object> getObjects() {
        if (!resultReady) throw new IllegalStateException("Parsing not completed");
        return this.objects;
    }

    public void startDocument() {
        this.resultReady = false;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        this.reflectiveClass = ReflectiveManager.getInstance().getReflectiveClass(localName);
//        this.currentObject = ReflectiveManager.getInstance().createReflectiveObject(reflectiveClass);
    }

    public void endElement(String uri, String localName, String qName) {

    }

    public void endDocument() {
        this.resultReady = true;
    }
}


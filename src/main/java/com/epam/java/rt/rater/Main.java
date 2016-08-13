package com.epam.java.rt.rater;

import com.epam.java.rt.rater.model.TariffList;
import com.epam.java.rt.rater.parser.DOMParser;
import com.epam.java.rt.rater.parser.ObjectParser;
import com.epam.java.rt.rater.parser.SAXParser;
import com.epam.java.rt.rater.parser.StAXParser;

/**
 * rater
 */
public class Main {
    public static void main(String[] args) {

        ObjectParser objectParser = ObjectParser.to(TariffList.class.getPackage());
        System.out.println(objectParser.parseXML(SAXParser.class, "xml/tariffs.xml"));
        System.out.println(objectParser.parseXML(StAXParser.class, "xml/tariffs.xml"));
        System.out.println(objectParser.parseXML(DOMParser.class, "xml/tariffs.xml"));

    }
}

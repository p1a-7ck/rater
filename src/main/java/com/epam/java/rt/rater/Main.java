package com.epam.java.rt.rater;

import com.epam.java.rt.rater.parser.ObjectParser;
import com.epam.java.rt.rater.parser.SAXParser;
import com.epam.java.rt.rater.service.FromStringAdapter;

import java.util.List;

/**
 * rater
 */
public class Main {
    public static void main(String[] args) {

        System.out.println(FromStringAdapter.convert("123", int.class) +
                FromStringAdapter.convert("123", int.class));

        Object object = ObjectParser.getParsedObject(SAXParser.class, "xml/tariffs.xml");

        System.out.println(object);

    }
}

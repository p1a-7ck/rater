package com.epam.java.rt.rater;

import com.epam.java.rt.rater.model.Tariff;
import com.epam.java.rt.rater.model.Tariffs;
import com.epam.java.rt.rater.model.reflective.ReflectiveClass;
import com.epam.java.rt.rater.parser.ObjectParser;
import com.epam.java.rt.rater.parser.SAXParser;
import com.epam.java.rt.rater.service.FromStringAdapter;
import com.epam.java.rt.rater.service.ReflectiveManager;

/**
 * rater
 */
public class Main {
    public static void main(String[] args) {

        System.out.println(FromStringAdapter.convert("123", int.class) +
                FromStringAdapter.convert("123", int.class));

        ReflectiveClass reflectiveClass = ReflectiveManager.getInstance()
                .getReflectiveClass(Tariff.class.getPackage().getName(), "Tariff");
        Object object_ = ReflectiveManager.getInstance().createReflectiveObject(reflectiveClass);
        object_ = ReflectiveManager.getInstance().buildImmutableReflectiveObject(reflectiveClass, object_);
        System.out.println(object_);

        ObjectParser objectParser = ObjectParser.to(Tariffs.class.getPackage());
        Object object = objectParser.parseXML(SAXParser.class, "xml/tariffs.xml");

        System.out.println(object);

    }
}

package com.epam.java.rt.rater.factory;

import com.epam.java.rt.rater.model.Tariff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * rater
 */
public class TariffsFactory {
    private static final Logger logger = LoggerFactory.getLogger(TariffsFactory.class);
    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    public static List<Tariff> createTariffs(String fileName) {
        List<Tariff> tariffs = new ArrayList<>();
        Tariff tariff = null;
        InputStream inputStream = null;
        XMLStreamReader streamReader = null;
        String chName;
        int chType;
        boolean parse = false;
        try {
            inputStream = TariffFactory.class.getClassLoader().getResourceAsStream(fileName);
            streamReader = inputFactory.createXMLStreamReader(inputStream);
            while (streamReader.hasNext()) {
                chType = streamReader.next();
                if (chType == XMLStreamConstants.START_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("tariffs")) {
                        parse = true;
                    } else if (parse && chName.equals("tariff")) {
                         tariff = TariffFactory.createTariff(streamReader);
                        if (tariff != null) tariffs.add(tariff);
                    }
                } else if (chType == XMLStreamConstants.END_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("tariffs")) parse = false;
                }
            }
        } catch (XMLStreamException exc) {
            logger.error("Parsing error:\n", exc);
        } catch (Exception exc) {
            logger.error("File error:\n", exc);
        }
        return tariffs;
    }
}
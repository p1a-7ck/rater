package com.epam.java.rt.rater.factory;

import com.epam.java.rt.rater.model.Tariff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.UUID;

/**
 * rater
 */
public class TariffFactory {
    private static final Logger logger = LoggerFactory.getLogger(TariffFactory.class);

    public static Tariff createTariff(XMLStreamReader streamReader) throws XMLStreamException {
        Tariff tariff = null;
        Tariff.Builder builder = null;
        String chName;
        int chType;
        while (streamReader.hasNext()) {
            chType = streamReader.next();
            if (chType == XMLStreamConstants.END_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals("tariff")) {
                    tariff = new Tariff(builder);
                    System.out.println(tariff.getOperator());
                    break;
                }
            } else if (chType == XMLStreamConstants.START_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals("id")) {
                    if (streamReader.getElementText().length() > 0) {
                        builder = Tariff.Builder.of(UUID.fromString(streamReader.getElementText()));
                    } else {
                        builder = Tariff.Builder.of(UUID.randomUUID());
                    }
                } else if (chName.equals("name") && builder != null) {
                    builder = builder.setName(streamReader.getElementText());
                } else if (chName.equals("operator") && builder != null) {
                    builder = builder.setOperator(streamReader.getElementText());
                } else if (chName.equals("services") && builder != null) {

                }
            }
        }
        return tariff;
    }

}


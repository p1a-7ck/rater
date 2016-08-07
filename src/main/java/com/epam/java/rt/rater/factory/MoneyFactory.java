package com.epam.java.rt.rater.factory;

import com.epam.java.rt.rater.model.Tariff;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.UUID;

/**
 * rater
 */
public class MoneyFactory {
    private static final Logger logger = LoggerFactory.getLogger(MoneyFactory.class);

    public static Money createMoney(XMLStreamReader streamReader, String chParentName) throws XMLStreamException {
        CurrencyUnit currencyUnit = null;
        Double amountValue = 0.0;
        String chName;
        int chType;
        while (streamReader.hasNext()) {
            chType = streamReader.next();
            if (chType == XMLStreamConstants.END_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals(chParentName)) return Money.of(currencyUnit, amountValue);
            } else if (chType == XMLStreamConstants.START_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals("currencyUnit")) {
                    currencyUnit = CurrencyUnit.of(streamReader.getElementText());
                } else if (chName.equals("amountValue")) {
                    amountValue = Double.valueOf(streamReader.getElementText());
                }
            }
        }
        return null;
    }
}

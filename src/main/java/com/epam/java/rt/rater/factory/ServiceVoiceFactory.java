package com.epam.java.rt.rater.factory;

import com.epam.java.rt.rater.model.CalculationVoiceSimple;
import com.epam.java.rt.rater.model.Service;
import com.epam.java.rt.rater.model.ServiceVoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;
import java.util.UUID;

/**
 * rater
 */
public class ServiceVoiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(TariffFactory.class);

    public static Service createServiceVoice(XMLStreamReader streamReader, List<Service> uniqueServices) throws XMLStreamException {
        logger.debug("createServiceVoice");
        Service service = null;
        ServiceVoice.Builder builder = null;
        String chName;
        int chType;
        boolean prepaid = false;
        boolean calculation = false;
        while (streamReader.hasNext()) {
            chType = streamReader.next();
            if (!prepaid && !calculation) {
                if (chType == XMLStreamConstants.END_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("serviceVoice")) {
                        logger.debug("serviceVoice.builder = {}", builder);
                        service = new ServiceVoice(builder);
                        for(int i = 0; i < uniqueServices.size(); i++) {
                            if (uniqueServices.get(i).equals(service))
                                return uniqueServices.get(i);
                        }
                        return service;
                    }
                } else if (chType == XMLStreamConstants.START_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("id")) {
                        if (streamReader.getElementText().length() > 0) {
                            builder = ServiceVoice.Builder.of(UUID.fromString(streamReader.getElementText()));
                        } else {
                            builder = ServiceVoice.Builder.of(UUID.randomUUID());
                        }
                    } else if (chName.equals("name") && builder != null) {
                        builder = builder.setName(streamReader.getElementText());
                    } else if (chName.equals("minSecondsCharging") && builder != null) {
                        builder = builder.setMinSecondsCharging(Integer.valueOf(streamReader.getElementText()));
                    } else if (chName.equals("oneSecondCost") && builder != null) {
                        builder = builder.setOneSecondCost(MoneyFactory.createMoney(streamReader, chName));
                    } else if (chName.equals("prepaid") && builder != null) {
                        prepaid = true;
                    } else if (chName.equals("calculation") && builder != null) {
                        calculation = true;
                    }
                }
            } else if (prepaid) {
                if (chType == XMLStreamConstants.END_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("prepaid")) prepaid = false;
                } else if (chType == XMLStreamConstants.START_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("prepaidSeconds") && builder != null) {
                        builder = builder.setPrepaidSeconds(Integer.valueOf(streamReader.getElementText()));
                    } else if (chName.equals("prepaidSecondsCost") && builder != null) {
                        builder = builder.setPrepaidSecondsCost(MoneyFactory.createMoney(streamReader, chName));
                    }
                }
            } else if (calculation) {
                if (chType == XMLStreamConstants.END_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("calculation")) calculation = false;
                } else if (chType == XMLStreamConstants.START_ELEMENT) {
                    chName = streamReader.getLocalName();
                    if (chName.equals("calculationVoiceSimple") && builder != null) {
                        builder = builder.setCalculation(CalculationVoiceSimple.getInstance());
                    }
                }
            }
        }
        return null;
    }

}

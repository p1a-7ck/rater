package com.epam.java.rt.rater.factory;

import com.epam.java.rt.rater.model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * rater
 */
public class ServicesFactory {
    private static final Logger logger = LoggerFactory.getLogger(ServicesFactory.class);

    public static List<Service> createServices(XMLStreamReader streamReader, List<Service> uniqueServices) throws XMLStreamException {
        List<Service> services = new ArrayList<>();
        Service service = null;
        String chName;
        int chType;
        while (streamReader.hasNext()) {
            chType = streamReader.next();
            if (chType == XMLStreamConstants.START_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals("serviceVoice")) {
                    service = ServiceVoiceFactory.createServiceVoice(streamReader, uniqueServices);
                    if (service != null) services.add(service);
                }
            } else if (chType == XMLStreamConstants.END_ELEMENT) {
                chName = streamReader.getLocalName();
                if (chName.equals("services")) break;
            }
        }
        return services;
    }
}

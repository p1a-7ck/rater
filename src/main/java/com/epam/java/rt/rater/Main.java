package com.epam.java.rt.rater;

import com.epam.java.rt.rater.factory.TariffsFactory;
import com.epam.java.rt.rater.model.Calculation;
import com.epam.java.rt.rater.model.CalculationVoiceSimple;
import com.epam.java.rt.rater.model.Tariff;

import java.util.List;

/**
 * rater
 */
public class Main {
    public static void main(String[] args) {
        Calculation calculation = CalculationVoiceSimple.getInstance();
        System.out.println(calculation);
        List<Tariff> tariffs = TariffsFactory.createTariffs("xml/tariffs.xml");
        for (int i = 0; i < tariffs.size(); i++) {
            System.out.println(tariffs.get(i));
        }

    }
}

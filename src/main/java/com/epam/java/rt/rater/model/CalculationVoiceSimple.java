package com.epam.java.rt.rater.model;

import org.joda.money.Money;

/**
 * rater
 */
public class CalculationVoiceSimple implements Calculation {
    private static CalculationVoiceSimple INSTANCE;

    private CalculationVoiceSimple() {
    }

    public static Calculation getInstance() {
        if (CalculationVoiceSimple.INSTANCE == null)
            CalculationVoiceSimple.INSTANCE = new CalculationVoiceSimple();
        return CalculationVoiceSimple.INSTANCE;
    }

    public static Money calculateCost(int value) {
        return null;
    }
}

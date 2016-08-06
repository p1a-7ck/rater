package com.epam.java.rt.rater.model;

import org.joda.money.Money;

/**
 * rater
 */
public interface Calculation {
    static Calculation getInstance() { return null; };
    static Money calculateCost(int value) { return null; };
}

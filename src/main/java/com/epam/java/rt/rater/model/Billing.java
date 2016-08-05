package com.epam.java.rt.rater.model;

import org.joda.money.Money;

/**
 * rater
 */
public interface Billing {
    Money calculateCost(int value);
}

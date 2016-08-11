package com.epam.java.rt.rater.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * rater
 */
public class Tariffs {
    private final List<Tariff> tariffList = new ArrayList<>();

    public Tariffs() {
    }

    public boolean addTariff(Tariff tariff) {
        return !this.tariffList.contains(tariff) && this.tariffList.add(tariff);
    }

    public Tariff removeTariff(int index) {
        return this.tariffList.remove(index);
    }

    public Tariff getTariff(int index) {
        return this.tariffList.get(index);
    }

    public int countTariffs() {
        return this.tariffList.size();
    }

    @Override
    public String toString() {
        return "Tariffs{" +
                "tariffList=" + tariffList +
                '}';
    }
}

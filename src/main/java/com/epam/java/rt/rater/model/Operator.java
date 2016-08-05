package com.epam.java.rt.rater.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * rater
 */
public class Operator {
    private final UUID id;
    private final String name;
    private final List<Tariff> tariffs;

    public Operator(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.tariffs = new ArrayList<>();
    }

    public static Operator of(String name) {
        return new Operator(UUID.randomUUID(), name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean addTarif(Tariff tariff) {
        return !this.tariffs.contains(tariff) && this.tariffs.add(tariff);
    }

    public Tariff removeTarif(int index) {
        return this.tariffs.remove(index);
    }

    public Tariff getTarif(int index) {
        return this.tariffs.get(index);
    }
}

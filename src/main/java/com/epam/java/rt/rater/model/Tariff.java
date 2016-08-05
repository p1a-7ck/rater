package com.epam.java.rt.rater.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * rater
 */
public class Tariff {
    private final UUID id;
    private final String name;
    private final Operator operator;
    private final List<Service> services;

    public Tariff(UUID id, String name, Operator operator) {
        this.id = id;
        this.name = name;
        this.operator = operator;
        this.services = new ArrayList<>();
    }

    public Tariff of(String name, Operator operator) {
        return new Tariff(UUID.randomUUID(), name, operator);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean addTarif(Service service) {
        return !this.services.contains(service) && this.services.add(service);
    }

    public Service removeTarif(int index) {
        return this.services.remove(index);
    }

    public Service getTarif(int index) {
        return this.services.get(index);
    }
}

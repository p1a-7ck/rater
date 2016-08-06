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
    private final String operator;
    private final List<Service> services;

    public static class Builder {
        private UUID id;
        private String name;
        private String operator;
        private List<Service> services;

        public static Builder of(UUID id) {
            Builder builder = new Builder();
            builder.id = id;
            return builder;
        }

        public Builder setName(String name) {
            if (name.length() == 0)
                throw new IllegalStateException("Name should be defined");
            this.name = name;
            return this;
        }

        public Builder setOperator(String operator) {
            if (operator.length() == 0)
                throw new IllegalStateException("Operator should be defined");
            this.operator = operator;
            return this;
        }

    }

    public Tariff(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.operator = builder.operator;
        this.services = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public boolean addService(Service service) {
        return !this.services.contains(service) && this.services.add(service);
    }

    public Service removeService(int index) {
        return this.services.remove(index);
    }

    public Service getService(int index) {
        return this.services.get(index);
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", services=" + services +
                '}';
    }
}

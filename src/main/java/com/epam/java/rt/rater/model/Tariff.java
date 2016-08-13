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
    private final List<Service> serviceList;

    public static class Builder {
        private UUID id;
        private String name;
        private String operator;
        private List<Service> serviceList;

        public Builder() {

        }

        public Builder setId(UUID id) {
            if (id == null)
                throw new IllegalArgumentException("Id should ne defined");
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            if (name.length() == 0)
                throw new IllegalArgumentException("Name should be defined");
            this.name = name;
            System.out.println("name set to '" + name + "'");
            return this;
        }

        public Builder setOperator(String operator) {
            if (operator.length() == 0)
                throw new IllegalArgumentException("Operator should be defined");
            this.operator = operator;
            return this;
        }

        public Builder addService(Service service) {
            if (service == null)
                throw new IllegalArgumentException("Services should be defined");
            if (this.serviceList == null) this.serviceList = new ArrayList<>();
            this.serviceList.add(service);
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", operator='" + operator + '\'' +
                    ", serviceList=" + serviceList +
                    '}';
        }
    }

    public Tariff(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.operator = builder.operator;
        this.serviceList = builder.serviceList;
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
        return !this.serviceList.contains(service) && this.serviceList.add(service);
    }

    public Service removeService(int index) {
        return this.serviceList.remove(index);
    }

    public Service getService(int index) {
        return this.serviceList.get(index);
    }

    public int countServices() {
        return this.serviceList.size();
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", serviceList=" + serviceList +
                '}';
    }
}

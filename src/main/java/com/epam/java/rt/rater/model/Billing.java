package com.epam.java.rt.rater.model;

/**
 * rater
 */
public class Billing {
    private final String name;

    public Billing(Builder builder) {
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Billing{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        String name;

        public Builder() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}

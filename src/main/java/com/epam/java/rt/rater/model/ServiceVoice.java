package com.epam.java.rt.rater.model;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * rater
 */
public class ServiceVoice implements Service {
    private final UUID id;
    private final String name;
    private final int minSecondsCharging;
    private final Money oneSecondCost;
    private final int prepaidSeconds;
    private final Money prepaidSecondsCost;
    private final Billing billing;

    public static class Builder {
        private final UUID id;
        private final String name;
        private int minSecondsCharging = 1;
        private Money oneSecondCost = Money.of(CurrencyUnit.USD, 0.20);
        private int prepaidSeconds = 0;
        private Money prepaidSecondsCost = Money.of(CurrencyUnit.USD, 0.0);
        private Billing billing = null;

        Builder(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Builder build(UUID id, String name) {
            return new Builder(id, name);
        }

        public Builder setMinSecondsCharging(int minSecondsCharging) {
            if (minSecondsCharging <=0 )
                throw new IllegalStateException("Minimum seconds charging should be more than zero");
            this.minSecondsCharging = minSecondsCharging;
            return this;
        }

        public Builder setOneSecondCost(Money oneSecondCost) {
            if (oneSecondCost.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalStateException("One second cost should be more than zero");
            this.oneSecondCost = oneSecondCost;
            return this;
        }

        public Builder setPrepaidSeconds(int prepaidSeconds) {
            if (prepaidSeconds <=0 )
                throw new IllegalStateException("Prepaid seconds should be more than zero");
            this.prepaidSeconds = prepaidSeconds;
            return this;
        }

        public Builder setPrepaidSecondsCost(Money prepaidSecondsCost) {
            if (prepaidSecondsCost.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalStateException("Prepaid seconds cost should be more than zero");
            this.prepaidSecondsCost = prepaidSecondsCost;
            return this;
        }

        public Builder setBilling(Billing billing) {
            if (billing == null)
                throw new IllegalStateException("Billing should be defined anyway");
            this.billing = billing;
            return this;
        }
    }

    public ServiceVoice(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.minSecondsCharging = builder.minSecondsCharging;
        this.oneSecondCost = builder.oneSecondCost;
        this.prepaidSeconds = builder.prepaidSeconds;
        this.prepaidSecondsCost = builder.prepaidSecondsCost;
        if (builder.billing == null) throw new IllegalStateException("There are no billing found");
        this.billing = builder.billing;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMinSecondsCharging() {
        return minSecondsCharging;
    }

    public Money getOneSecondCost() {
        return oneSecondCost;
    }

    public int getPrepaidSeconds() {
        return prepaidSeconds;
    }

    public Money getPrepaidSecondsCost() {
        return prepaidSecondsCost;
    }

    public Billing getBilling() {
        return billing;
    }

    @Override
    public String toString() {
        return "ServiceVoice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

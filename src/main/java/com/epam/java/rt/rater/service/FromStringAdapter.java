package com.epam.java.rt.rater.service;

import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * rater
 */
public final class FromStringAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FromStringAdapter.class);
    private static Map<Class, Function<String, ?>> adapters;

    private FromStringAdapter() {
    }

    static {
        // primitives and classes
        FromStringAdapter.adapters = new HashMap<>();
        FromStringAdapter.adapters.put(long.class, (stringValue) -> {return (long) Long.parseLong(stringValue);});
        FromStringAdapter.adapters.put(Long.class, (stringValue) -> {return Long.parseLong(stringValue);});
        FromStringAdapter.adapters.put(int.class, (stringValue) -> {return (int) Integer.parseInt(stringValue);});
        FromStringAdapter.adapters.put(Integer.class, (stringValue) -> {return Integer.parseInt(stringValue);});
        FromStringAdapter.adapters.put(char.class, (stringValue) -> {return stringValue.charAt(0);});
        FromStringAdapter.adapters.put(Character.class, (stringValue) -> {return Character.valueOf(stringValue.charAt(0));});
        FromStringAdapter.adapters.put(short.class, (stringValue) -> {return (short) Short.parseShort(stringValue);});
        FromStringAdapter.adapters.put(Short.class, (stringValue) -> {return Short.parseShort(stringValue);});
        FromStringAdapter.adapters.put(byte.class, (stringValue) -> {return (byte) Byte.parseByte(stringValue);});
        FromStringAdapter.adapters.put(Byte.class, (stringValue) -> {return Byte.parseByte(stringValue);});
        FromStringAdapter.adapters.put(double.class, (stringValue) -> {return (double) Double.parseDouble(stringValue);});
        FromStringAdapter.adapters.put(Double.class, (stringValue) -> {return Double.parseDouble(stringValue);});
        FromStringAdapter.adapters.put(float.class, (stringValue) -> {return (float) Float.parseFloat(stringValue);});
        FromStringAdapter.adapters.put(Float.class, (stringValue) -> {return Float.parseFloat(stringValue);});
        FromStringAdapter.adapters.put(boolean.class, (stringValue) -> {return (boolean) Boolean.parseBoolean(stringValue);});
        FromStringAdapter.adapters.put(Boolean.class, (stringValue) -> {return Boolean.parseBoolean(stringValue);});
        FromStringAdapter.adapters.put(String.class, (stringValue) -> {return stringValue;});
        // extra classes
        FromStringAdapter.adapters.put(UUID.class, (stringValue) -> {return UUID.fromString(stringValue);});
        FromStringAdapter.adapters.put(Money.class, (stringValue) -> {return Money.parse(stringValue);});
    }

    public static <T> T convert(Class<T> resultValueClass, String stringValue) {
        try {
            return (T) FromStringAdapter.adapters.get(resultValueClass).apply(stringValue);
        } catch (Exception exc) {
            logger.error("String convert to '{}' error ({})", resultValueClass, exc.getMessage());
        }
        return null;
    }
}

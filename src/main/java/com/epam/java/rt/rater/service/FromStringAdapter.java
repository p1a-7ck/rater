package com.epam.java.rt.rater.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * rater
 */
public final class FromStringAdapter {
    private static Map<Class, Function<String, ?>> adapters;

    private FromStringAdapter() {
    }

    static {
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
    }

    public static <T> T convert(String stringValue, Class<T> resultValueClass) {
        return (T) FromStringAdapter.adapters.get(resultValueClass).apply(stringValue);
    }
}

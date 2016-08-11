package com.epam.java.rt.rater;

import com.epam.java.rt.rater.service.FromStringAdapter;

/**
 * rater
 */
public class Main {
    public static void main(String[] args) {

        System.out.println(FromStringAdapter.convert("123", int.class) +
                FromStringAdapter.convert("123", int.class));




    }
}

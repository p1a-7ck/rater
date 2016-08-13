package com.epam.java.rt.rater.parser;

import java.io.InputStream;

/**
 * rater
 */
interface Parser {
    void parse(ObjectHandler objectHandler, InputStream inputStream);
}

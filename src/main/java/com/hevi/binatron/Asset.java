package com.hevi.binatron;

import java.util.Locale;

public class Asset {
    private final String name;

    public Asset(String name) {
        this.name = name.toUpperCase(Locale.ENGLISH);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrendEvent extends ApplicationEvent {
    Type trendType;
    LocalDateTime dateTime;
    String price;

    enum Type {
        UPPER_BOUND_BROKEN,
        LOWER_BOUND_BROKEN
    }

    public TrendEvent(Object source, Type trendType, LocalDateTime dateTime, String price) {
        super(source);
        this.trendType = trendType;
        this.dateTime = dateTime;
        this.price = price;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        if (trendType == Type.UPPER_BOUND_BROKEN) {
            return "Upper bound of " + price + " is broken at " + dateTime.format(formatter);
        }
        return "Lower bound of " + price + " is broken at " + dateTime.format(formatter);
    }
}

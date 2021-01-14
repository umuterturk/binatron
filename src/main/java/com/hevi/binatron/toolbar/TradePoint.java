package com.hevi.binatron.toolbar;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TradePoint {

    final Double price;
    final Double epoch;

    public TradePoint(String dateTime, double price) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        this.price = price;
        this.epoch = Long.valueOf(localDateTime.toEpochSecond(ZoneOffset.UTC)).doubleValue();
    }

    public TradePoint(String dateTime, String price) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        this.price = Double.valueOf(price);
        this.epoch = Long.valueOf(localDateTime.toEpochSecond(ZoneOffset.UTC)).doubleValue();
    }

    public TradePoint(Long epoch, double price) {
        this.price = price;
        this.epoch = epoch.doubleValue();
    }

    public TradePoint(Double epoch, double price) {
        this.price = price;
        this.epoch = epoch;
    }

    strictfp public TradePoint mid(TradePoint tradePoint) {
        return new TradePoint(epoch / 2.0 + tradePoint.epoch / 2.0, price / 2.0 + tradePoint.price / 2.0);
    }
}
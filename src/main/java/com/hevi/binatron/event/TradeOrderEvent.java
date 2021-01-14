package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TradeOrderEvent extends ApplicationEvent {
    Double price;
    LocalDateTime epoch;
    Action action;

    public TradeOrderEvent(Object source, Double price, LocalDateTime epoch, Action action) {
        super(source);
        this.price = price;
        this.epoch = epoch;
        this.action = action;
    }

    public enum Action {
        SOLD_UPPER,
        SOLD_STOP_LOSS,
        BOUGHT
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        if (action == Action.SOLD_STOP_LOSS) {
            return "Sorry I got to sell your stock from $" + price + " as you reached a stop-loss point at " + epoch.format(formatter);
        } else if (action == Action.SOLD_UPPER) {
            return "You got rich MF! I sold your stocks from $" + price + " as you reached the upper bound at " + epoch.format(formatter);
        }
        return "TradeEvent{" +
                "price=" + price +
                ", epoch=" + epoch +
                ", action=" + action +
                '}';
    }

    public Double getPrice() {
        return price;
    }

    public LocalDateTime getEpoch() {
        return epoch;
    }

    public Action getAction() {
        return action;
    }
}

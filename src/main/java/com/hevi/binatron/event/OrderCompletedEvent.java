package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

public class OrderCompletedEvent extends ApplicationEvent {
    String symbol;
    String totalQuantity;
    String totalPrice;
    String orderType;

    public OrderCompletedEvent(Object source, String symbol, String totalQuantity, String totalPrice, String orderType) {
        super(source);
        this.symbol = symbol;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return orderType + " order is completed: " +
                "symbol='" + symbol +
                ", totalQuantity='" + totalQuantity +
                ", totalPrice='" + totalPrice;
    }
}

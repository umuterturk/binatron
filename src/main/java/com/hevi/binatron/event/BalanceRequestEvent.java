package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class BalanceRequestEvent extends ApplicationEvent {
    String symbol;

    public BalanceRequestEvent(Object source, String symbol) {
        super(source);
        this.symbol = symbol.toUpperCase(Locale.ENGLISH);
    }

    public String getSymbol() {
        return symbol;
    }
}

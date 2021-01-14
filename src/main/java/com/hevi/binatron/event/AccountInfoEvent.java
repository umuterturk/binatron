package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

public class AccountInfoEvent extends ApplicationEvent {
    String assetType;
    String assetBalance;

    public AccountInfoEvent(Object source, String assetType, String assetBalance) {
        super(source);
        this.assetType = assetType;
        this.assetBalance = assetBalance;
    }

    @Override
    public String toString() {
        return "You have free " + assetBalance + ' ' + assetType;
    }
}

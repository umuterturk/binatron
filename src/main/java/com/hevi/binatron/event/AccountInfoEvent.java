package com.hevi.binatron.event;

import com.hevi.binatron.Asset;
import org.springframework.context.ApplicationEvent;

public class AccountInfoEvent extends ApplicationEvent {
    Asset asset;
    String assetBalance;

    public AccountInfoEvent(Object source, Asset asset, String assetBalance) {
        super(source);
        this.asset = asset;
        this.assetBalance = assetBalance;
    }

    @Override
    public String toString() {
        return "You have free " + assetBalance + ' ' + asset.name();
    }
}

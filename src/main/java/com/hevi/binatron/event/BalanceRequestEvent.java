package com.hevi.binatron.event;

import com.hevi.binatron.Asset;
import org.springframework.context.ApplicationEvent;

public class BalanceRequestEvent extends ApplicationEvent {
    Asset asset;

    public BalanceRequestEvent(Object source, Asset asset) {
        super(source);
        this.asset = asset;
    }

    public Asset getSymbol() {
        return asset;
    }
}

package com.hevi.binatron.event;

import com.hevi.binatron.Asset;
import org.springframework.context.ApplicationEvent;

public class AssetInfoRequestEvent extends ApplicationEvent {
    Asset asset;

    public AssetInfoRequestEvent(Object source, Asset asset) {
        super(source);
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }
}

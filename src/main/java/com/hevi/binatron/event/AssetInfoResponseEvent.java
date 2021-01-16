package com.hevi.binatron.event;

import com.hevi.binatron.Asset;
import org.springframework.context.ApplicationEvent;

public class AssetInfoResponseEvent extends ApplicationEvent {
    Asset asset;
    String currentPrice;
    String volume;
    String lowest;
    String highest;
    String weightedAveragePrice;

    public AssetInfoResponseEvent(Object source, Asset asset, String currentPrice, String volume, String lowest, String highest, String weightedAveragePrice) {
        super(source);
        this.asset = asset;
        this.currentPrice = currentPrice;
        this.volume = volume;
        this.lowest = lowest;
        this.highest = highest;
        this.weightedAveragePrice = weightedAveragePrice;
    }

    public Asset getAsset() {
        return asset;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public String getVolume() {
        return volume;
    }

    public String getLowest() {
        return lowest;
    }

    public String getHighest() {
        return highest;
    }

    public String getWeightedAveragePrice() {
        return weightedAveragePrice;
    }

    @Override
    public String toString() {
        return "Stats for " +
                "asset " + asset + " is as follows " +
                ", currentPrice=" + currentPrice +
                ", volume=" + volume +
                ", lowest=" + lowest +
                ", highest=" + highest +
                ", weightedAveragePrice=" + weightedAveragePrice;
    }
}

package com.hevi.binatron.toolbar;

public class PitchFork {
    final TradePoint tradePoint0;
    final TradePoint tradePoint1;
    final TradePoint tradePoint2;
    final double m;

    public PitchFork(TradePoint tradePoint0, TradePoint tradePoint1, TradePoint tradePoint2) {
        this.tradePoint0 = tradePoint0;
        this.tradePoint1 = tradePoint1;
        this.tradePoint2 = tradePoint2;
        TradePoint tradePoint3 = tradePoint1.mid(tradePoint2);
        m = getM(tradePoint0, tradePoint3);
    }

    strictfp private double getM(TradePoint tradePoint0, TradePoint tradePoint3) {
        return (tradePoint3.price - tradePoint0.price) / (tradePoint3.epoch - tradePoint0.epoch);
    }

    strictfp double at(TradePoint tradePoint, Double epoch) {
        return tradePoint.price + m * (epoch - tradePoint.epoch);
    }


    double at(Double p, Double epoch) {

        double mid = at(tradePoint0, epoch);
        double high = at(tradePoint1, epoch);
        return mid + p * (high - mid);
    }




}

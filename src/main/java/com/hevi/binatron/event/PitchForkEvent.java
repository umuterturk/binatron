package com.hevi.binatron.event;

import com.hevi.binatron.toolbar.TradePoint;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class PitchForkEvent extends ApplicationEvent {
    List<TradePoint> tradePointList;

    public PitchForkEvent(Object source, List<TradePoint> tradePointList) {
        super(source);
        this.tradePointList = tradePointList;
    }

    public PitchForkEvent(Object source) {
        super(source);
    }

    public List<TradePoint> getTradePointList() {
        return tradePointList;
    }
}

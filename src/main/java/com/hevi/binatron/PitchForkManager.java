package com.hevi.binatron;

import com.hevi.binatron.event.PitchForkEvent;
import com.hevi.binatron.event.TradeLifecycleEvent;
import com.hevi.binatron.toolbar.PitchFork;
import com.hevi.binatron.toolbar.PitchForkInstance;
import com.hevi.binatron.toolbar.TradePoint;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PitchForkManager {
    PitchFork pitchFork;

    final ApplicationEventMulticaster simpleApplicationEventMulticaster;

    public PitchForkManager(ApplicationEventMulticaster simpleApplicationEventMulticaster) {
        this.simpleApplicationEventMulticaster = simpleApplicationEventMulticaster;
    }

    synchronized public void setPitchFork(TradePoint tp1, TradePoint tp2, TradePoint tp3) {
        this.pitchFork = new PitchFork(tp1, tp2, tp3);
        System.out.println(pitchFork);
    }

    synchronized public PitchForkInstance getPitchForkInstance() {
        if (pitchFork == null) return null;
        return new PitchForkInstance(pitchFork);
    }

    @EventListener
    synchronized public void handlePitchForkEvent(PitchForkEvent pitchForkEvent) {
        final List<TradePoint> tradePointList = pitchForkEvent.getTradePointList();
        if (tradePointList == null) {
            pitchFork = null;
        } else {
            simpleApplicationEventMulticaster.multicastEvent(new TradeLifecycleEvent(this, TradeLifecycleEvent.LifeCycleType.STOP));
            setPitchFork(tradePointList.get(0), tradePointList.get(1), tradePointList.get(2));
        }
    }
}

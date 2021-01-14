package com.hevi.binatron;

import com.hevi.binatron.event.TradeLifecycleEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationState {
    boolean isRunning = false;

    synchronized public boolean isRunning() {
        return isRunning;
    }

    synchronized public void setRunning(boolean running) {
        isRunning = running;
    }

    @EventListener
    public void handleTradeLifecycleEvent(TradeLifecycleEvent lifecycleEvent) {
        setRunning(lifecycleEvent.getLifeCycleType() == TradeLifecycleEvent.LifeCycleType.START);
    }
}

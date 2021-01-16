package com.hevi.binatron;

import com.hevi.binatron.configuration.TradingSymbols;
import com.hevi.binatron.event.TradeLifecycleEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({TradingSymbols.class})
public class ApplicationState {
    boolean isRunning = false;
    final
    TradingSymbols tradingSymbols;

    public ApplicationState(TradingSymbols tradingSymbols) {
        this.tradingSymbols = tradingSymbols;
    }

    public TradingSymbols tradingSymbols() {
        return tradingSymbols;
    }

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

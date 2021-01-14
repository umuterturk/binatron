package com.hevi.binatron;

import com.hevi.binatron.event.TradeOrderEvent;
import com.hevi.binatron.toolbar.PitchForkInstance;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Broker {

    public Broker(ApplicationEventMulticaster simpleApplicationEventMulticaster, PitchForkManager pitchForkManager, ApplicationState applicationState) {
        this.simpleApplicationEventMulticaster = simpleApplicationEventMulticaster;
        this.pitchForkManager = pitchForkManager;
        this.applicationState = applicationState;
    }


    final ApplicationState applicationState;

    final ApplicationEventMulticaster simpleApplicationEventMulticaster;
    final PitchForkManager pitchForkManager;


    boolean isActive = true;
    Area previousArea = Area.NONE;
    Transition previousTransition = Transition.NONE;

    enum TrendAction {
        NONE,
        TREND_BROKE_UPPER,
        TREND_BROKE_LOWER
    }

    enum TradeAction {
        NONE,
        SELL_HIGHEST,
        SELL_HIGH,
        SELL_LOWEST, // STOP LOSS
        BUY_LOW
    }

    enum Transition {
        NONE,
        SAME,
        MID_TO_LOW,
        LOW_TO_LOWEST,
        MID_TO_HIGH,
        HIGH_TO_HIGHEST,
        OTHER
    }

    enum Area {
        NONE,
        HIGHEST,
        HIGH,
        MID,
        LOW,
        LOWEST
    }

    synchronized public void process(String currentPriceStr) {
        PitchForkInstance pitchForkInstance = pitchForkManager.getPitchForkInstance();
        if (applicationState.isRunning() && isActive && pitchForkInstance != null) {
            Area currentArea;
            double currentPrice = Double.parseDouble(currentPriceStr);
            if (currentPrice > pitchForkInstance.high()) {
                currentArea = Area.HIGHEST;
            } else if (currentPrice > pitchForkInstance.at(0.382)) {
                currentArea = Area.HIGH;
            } else if (currentPrice > pitchForkInstance.at(-0.382)) {
                currentArea = Area.MID;
            } else if (currentPrice > pitchForkInstance.low()) {
                currentArea = Area.LOW;
            } else {
                currentArea = Area.LOWEST;
            }

            Transition currentTransition = Transition.OTHER;
            if (previousArea == currentArea) {
                currentTransition = Transition.SAME;
            } else if (previousArea == Area.HIGH && currentArea == Area.HIGHEST) {
                currentTransition = Transition.HIGH_TO_HIGHEST;
            } else if (previousArea == Area.MID && currentArea == Area.HIGH) {
                currentTransition = Transition.MID_TO_HIGH;
            } else if (previousArea == Area.LOW && currentArea == Area.LOWEST) {
                currentTransition = Transition.LOW_TO_LOWEST;
            } else if (previousArea == Area.MID && currentArea == Area.LOW) {
                currentTransition = Transition.MID_TO_LOW;
            }

            TradeAction tradeAction = TradeAction.NONE;
            if (currentTransition == Transition.MID_TO_HIGH && (previousTransition == Transition.NONE || previousTransition == Transition.MID_TO_LOW)) {
                tradeAction = TradeAction.SELL_HIGH;
            } else if (currentTransition == Transition.HIGH_TO_HIGHEST && (previousTransition == Transition.NONE || previousTransition == Transition.MID_TO_LOW)) {
                tradeAction = TradeAction.SELL_HIGHEST;
            } else if (currentTransition == Transition.MID_TO_LOW && (previousTransition == Transition.NONE || previousTransition == Transition.HIGH_TO_HIGHEST || previousTransition == Transition.MID_TO_HIGH)) {
                tradeAction = TradeAction.BUY_LOW;
            } else if (currentTransition == Transition.LOW_TO_LOWEST && (previousTransition == Transition.NONE || previousTransition == Transition.MID_TO_LOW)) {
                tradeAction = TradeAction.SELL_LOWEST;
            }

            if (tradeAction != TradeAction.NONE) {
                previousTransition = currentTransition;
            }

            TrendAction trendAction = TrendAction.NONE;
            if (tradeAction == TradeAction.SELL_HIGHEST) {
                trendAction = TrendAction.TREND_BROKE_UPPER;
            } else if (tradeAction == TradeAction.SELL_LOWEST) {
                trendAction = TrendAction.TREND_BROKE_LOWER;
            }

            if (trendAction != TrendAction.NONE) {
                isActive = false;
            }

            if (tradeAction == TradeAction.SELL_HIGH || tradeAction == TradeAction.SELL_HIGHEST || tradeAction == TradeAction.SELL_LOWEST) {
                simpleApplicationEventMulticaster.multicastEvent(
                        new TradeOrderEvent(this, currentPrice, LocalDateTime.now(), tradeAction == TradeAction.SELL_LOWEST ? TradeOrderEvent.Action.SOLD_STOP_LOSS : TradeOrderEvent.Action.SOLD_UPPER));
            } else if (tradeAction == TradeAction.BUY_LOW) {
                simpleApplicationEventMulticaster.multicastEvent(new TradeOrderEvent(this, currentPrice, LocalDateTime.now(), TradeOrderEvent.Action.BOUGHT));
            }

            previousArea = currentArea;

            System.out.println("price=" + currentPriceStr + " area=" + currentArea.name() + " transition=" + previousTransition.name() + " trade action=" + tradeAction.name() + " trend action=" + trendAction.name());
        }
    }
}

package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

public class TradeLifecycleEvent extends ApplicationEvent {
    public enum LifeCycleType {
        START,
        STOP
    }

    LifeCycleType lifeCycleType;

    public TradeLifecycleEvent(Object source, LifeCycleType lifeCycleType) {
        super(source);
        this.lifeCycleType = lifeCycleType;
    }

    public LifeCycleType getLifeCycleType() {
        return lifeCycleType;
    }
}

package com.hevi.binatron.event;

import org.springframework.context.ApplicationEvent;

public class InformativeMessageEvent extends ApplicationEvent {
    String message;

    public InformativeMessageEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

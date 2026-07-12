package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

public class UpdateAllEstoqueEvent extends ApplicationEvent {
    public UpdateAllEstoqueEvent(Object source) {
        super(source);
    }
}
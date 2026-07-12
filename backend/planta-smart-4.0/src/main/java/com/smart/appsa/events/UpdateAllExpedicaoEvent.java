package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

public class UpdateAllExpedicaoEvent extends ApplicationEvent{

    public UpdateAllExpedicaoEvent(Object source) {
        super(source);
    }

}

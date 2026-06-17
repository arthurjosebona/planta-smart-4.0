package com.smart.appsa.config;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AppStateConfig {
    // Permite visualização entre diferentes threads
    private volatile boolean readOnly;
}

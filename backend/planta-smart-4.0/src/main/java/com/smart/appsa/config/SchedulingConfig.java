package com.smart.appsa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Habilita o suporte a {@code @Scheduled} (usado pelo tick de 1s do cronômetro de produção).
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

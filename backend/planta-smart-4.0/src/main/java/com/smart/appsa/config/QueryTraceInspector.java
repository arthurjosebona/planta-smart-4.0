package com.smart.appsa.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryTraceInspector implements StatementInspector {
    private static final Logger log = LoggerFactory.getLogger(QueryTraceInspector.class);

    @Override
    public String inspect(String sql) {
        if (sql.contains("t_sa_expedicao")) {
            log.warn("=== QUERY ORIGIN TRACE ===");
            // Imprime os primeiros frames relevantes do stack
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement el : stack) {
                if (el.getClassName().startsWith("com.smart")) {
                    log.warn("  at {}", el);
                }
            }
        }
        return sql;
    }
}
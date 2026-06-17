package com.smart.appsa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "clp")
public class ClpIpConfig {
    private Map<String, String> ips = new HashMap<>();

    public Map<String, String> getIps() { return ips; }
    public void setIps(Map<String, String> ips) { this.ips = ips; }

    public String getIp(String clpName) { return ips.get(clpName); }
    public void setIp(String clpName, String ip) { ips.put(clpName, ip); }
}
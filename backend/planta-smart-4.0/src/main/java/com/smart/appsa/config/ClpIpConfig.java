package com.smart.appsa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "clp")
public class ClpIpConfig {

    private Map<String, String> ips = new HashMap<>();
    private Map<String, ClpEndpoint> endpoints = new HashMap<>();

    public Map<String, String> getIps() { return ips; }
    public void setIps(Map<String, String> ips) { this.ips = ips; }

    public String getIp(String clpName) { return ips.get(clpName); }
    public void setIp(String clpName, String ip) { ips.put(clpName, ip); }

    public String getEstoqueIp()   { return ips.get("estoque"); }
    public String getProcessoIp()  { return ips.get("processo"); }
    public String getMontagemIp()  { return ips.get("montagem"); }
    public String getExpedicaoIp() { return ips.get("expedicao"); }

    // extras para endpoints com porta
    public Map<String, ClpEndpoint> getEndpoints() { return endpoints; }
    public void setEndpoints(Map<String, ClpEndpoint> endpoints) { this.endpoints = endpoints; }

    public ClpEndpoint getEndpoint(String clpName) { return endpoints.get(clpName); }

    @Data
    public static class ClpEndpoint {
        private String ip;
        private int porta = 102;
    }
}
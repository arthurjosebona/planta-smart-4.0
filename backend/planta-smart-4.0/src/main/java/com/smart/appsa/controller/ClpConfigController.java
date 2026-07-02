package com.smart.appsa.controller;

import com.smart.appsa.config.ClpIpConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/config/clp")
public class ClpConfigController {

    private final ClpIpConfig clpIpConfig;

    public ClpConfigController(ClpIpConfig clpIpConfig) {
        this.clpIpConfig = clpIpConfig;
    }

    @GetMapping("/ips")
    public ResponseEntity<Map<String, String>> getAllIps() {
        return ResponseEntity.ok(clpIpConfig.getIps());
    }

    @GetMapping("/ips/{clp}")
    public ResponseEntity<String> getIp(@PathVariable String clp) {
        String ip = clpIpConfig.getIp(clp);
        if (ip == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ip);
    }

    @PutMapping("/ips/{clp}")
    public ResponseEntity<Void> setIp(
            @PathVariable String clp,
            @RequestBody String novoIp) {
        clpIpConfig.setIp(clp, novoIp.trim());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ips")
    public ResponseEntity<Void> setAllIps(@RequestBody Map<String, String> novosIps) {
        log.info("Atualizando IPs dos CLPs: {}", novosIps);
        novosIps.forEach(clpIpConfig::setIp);
        return ResponseEntity.noContent().build();
    }
}
package com.smart.appsa.dto.clp;

import lombok.Builder;

@Builder
public record ClpStatusPingDTO(
    String nome,
    String ip,
    boolean online,
    String verifiedAt
) {}

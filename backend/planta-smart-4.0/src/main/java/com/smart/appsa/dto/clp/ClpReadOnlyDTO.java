package com.smart.appsa.dto.clp;

import lombok.Builder;

@Builder
public record ClpReadOnlyDTO(
    Boolean readOnly
) {}

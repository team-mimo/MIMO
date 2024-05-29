package com.ssafy.mimo.domain.hub.dto;

import lombok.Builder;

@Builder
public record HubRegisterRequestDto(
        String serialNumber,
        Long houseId
) {}

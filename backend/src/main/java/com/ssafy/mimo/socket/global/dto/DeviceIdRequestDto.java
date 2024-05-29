package com.ssafy.mimo.socket.global.dto;

import lombok.Builder;

@Builder
public record DeviceIdRequestDto(
        String type,
        String requestName,
        String machineType,
        String macAddress
) {
}

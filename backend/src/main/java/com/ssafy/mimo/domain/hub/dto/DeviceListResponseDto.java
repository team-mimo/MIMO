package com.ssafy.mimo.domain.hub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceListResponseDto {
    Long deviceId;
    String macAddress;
    String deviceType;
}

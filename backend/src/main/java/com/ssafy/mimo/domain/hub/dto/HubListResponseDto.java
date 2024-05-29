package com.ssafy.mimo.domain.hub.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubListResponseDto {
    Long hubId;
    String serialNumber;
    LocalDateTime registeredDttm;
    List<DeviceListResponseDto> devices;
}

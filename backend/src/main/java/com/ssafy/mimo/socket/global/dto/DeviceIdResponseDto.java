package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceIdResponseDto {
    private String type;
    private String requestName;
    private String machineType;
    private String macAddress;
    private Long id;
    public DeviceIdResponseDto(DeviceIdRequestDto deviceIdRequestDto, Long id) {
        this.type = deviceIdRequestDto.type();
        this.requestName = deviceIdRequestDto.requestName();
        this.machineType = deviceIdRequestDto.machineType();
        this.macAddress = deviceIdRequestDto.macAddress();
        this.id = id;
    }
}

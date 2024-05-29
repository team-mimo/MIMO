package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightControlResponseDto {
    private String type;
    private Long lightId;
    @Builder.Default
    private LightControlResponseDataDto data = new LightControlResponseDataDto();
    @Override
    public String toString() {
        return "{\"type\":\"" + type +
                "\", \"lightId\":" + lightId +
                ", \"data\":" + data.toString() +
                '}';
    }
    public LightControlResponseDto(LightControlRequestDto lightControlRequestDto, LightControlRequestDataDto lightControlRequestDataDto, String curColor) {
        this.type = lightControlRequestDto.getType();
        this.lightId = lightControlRequestDto.getLightId();
        this.data = new LightControlResponseDataDto(lightControlRequestDataDto, curColor);
    }
}

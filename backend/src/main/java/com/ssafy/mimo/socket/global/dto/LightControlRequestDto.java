package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightControlRequestDto {
    private String type;
    private Long lightId;
    @Builder.Default
    private LightControlRequestDataDto data = new LightControlRequestDataDto();
    @Override
    public String toString() {
        return "{\"type\":\"" + type +
                "\", \"lightId\":" + lightId +
                ", \"data\":" + data.toString() +
                '}';
    }
}

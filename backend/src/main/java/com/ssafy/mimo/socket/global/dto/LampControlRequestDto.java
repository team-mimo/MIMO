package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LampControlRequestDto {
    private String type;
    private Long lampId;
    @Builder.Default
    private LampControlRequestDataDto data = new LampControlRequestDataDto();
    @Override
    public String toString() {
        return "{\"type\":\"" + type +
                "\", \"lampId\":" + lampId +
                ", \"data\":" + data.toString() +
                '}';
    }
}

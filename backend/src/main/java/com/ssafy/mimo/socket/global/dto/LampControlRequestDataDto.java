package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LampControlRequestDataDto {
    @Builder.Default
    private String requestName = "request";
    @Override
    public String toString() {
        return "{\"requestName\":\"" + requestName +
                "\"}";
    }
}
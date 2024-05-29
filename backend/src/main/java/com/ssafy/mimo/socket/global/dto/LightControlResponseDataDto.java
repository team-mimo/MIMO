package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LightControlResponseDataDto {
    @Builder.Default
    private String requestName = "request";
    @Builder.Default
    private String curColor = "default";
    @Override
    public String toString() {
        return "{\"requestName\":\"" + requestName +
                "\", \"curColor\":\"" + curColor +
                "\"}";
    }
    public LightControlResponseDataDto(LightControlRequestDataDto lightControlRequestDataDto, String curColor) {
        this.requestName = lightControlRequestDataDto.getRequestName();
        this.curColor = curColor;
    }
}

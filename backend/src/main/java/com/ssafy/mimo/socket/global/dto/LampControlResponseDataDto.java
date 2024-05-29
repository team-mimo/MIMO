package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LampControlResponseDataDto {
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
    public LampControlResponseDataDto(LampControlRequestDataDto lampControlRequestDataDto, String curColor) {
        this.requestName = lampControlRequestDataDto.getRequestName();
        this.curColor = curColor;
    }
}

package com.ssafy.mimo.domain.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualControlRequestDataDto {
    @Builder.Default
    private String requestName = "request";
    @Builder.Default
    private String color = "default";
    @Builder.Default
    private Integer time = null;
    @Builder.Default
    private Integer state = null;
    @Override
    public String toString() {
        return "{\"requestName\":\"" + requestName +
                "\", \"color\":\"" + color +
                "\", \"time\":" + time +
                ", \"state\":" + state +
                '}';
    }
}

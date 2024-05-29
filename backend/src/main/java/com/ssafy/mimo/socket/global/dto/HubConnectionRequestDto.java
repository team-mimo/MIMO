package com.ssafy.mimo.socket.global.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubConnectionRequestDto {
        String type;
        String requestName;
        String hubSerialNumber;
}

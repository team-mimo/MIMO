package com.ssafy.mimo.socket.global.dto;

import lombok.Builder;

@Builder
public record HubConnectionResponseDto(
        String type,
        String requestName,
        Long hubId
) {}

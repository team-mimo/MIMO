package com.ssafy.mimo.user.dto;

import lombok.Builder;

@Builder
public record MyInfoResponseDto(
        Long userId,
        boolean hasHome,
        boolean hasHub) {

}

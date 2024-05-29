package com.ssafy.mimo.domain.house.dto;

import lombok.Builder;

@Builder
public record NewHouseRequestDto(String address,
                                 String nickname) {
}

package com.ssafy.mimo.domain.house.dto;

import lombok.Builder;

@Builder
public record OldHouseResponseDto(Long houseId,
                                  String address) {
}
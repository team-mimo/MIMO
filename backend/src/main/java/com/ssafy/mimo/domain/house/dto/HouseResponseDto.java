package com.ssafy.mimo.domain.house.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HouseResponseDto(Long houseId,
                               String nickname,
                               String address,
                               boolean isHome,
                               List<String> devices) {
}

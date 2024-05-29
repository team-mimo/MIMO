package com.ssafy.mimo.domain.house.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HouseDeviceListResponseDto(Long houseId,
                                         String nickname,
                                         String address,
                                         boolean isHome,
                                         List<DeviceListDto> devices) {
}

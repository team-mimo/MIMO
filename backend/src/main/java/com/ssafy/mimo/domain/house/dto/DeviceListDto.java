package com.ssafy.mimo.domain.house.dto;

import lombok.Builder;

@Builder
public record DeviceListDto(Long userId,
                            Long hubId,
                            Long deviceId,
                            String type,
                            String nickname,
                            boolean isAccessible) {
}

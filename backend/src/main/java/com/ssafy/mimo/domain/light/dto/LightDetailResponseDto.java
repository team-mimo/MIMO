package com.ssafy.mimo.domain.light.dto;

import lombok.Builder;

@Builder
public record LightDetailResponseDto(
	Long lightId,
	String nickname,
	String wakeupColor,
	String curColor,
	String macAddress,
	boolean isAccessible
) {
}

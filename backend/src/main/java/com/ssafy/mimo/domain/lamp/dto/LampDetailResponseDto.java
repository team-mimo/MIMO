package com.ssafy.mimo.domain.lamp.dto;

import lombok.Builder;

@Builder
public record LampDetailResponseDto(
	Long lampId,
	String nickname,
	String wakeupColor,
	String curColor,
	String macAddress,
	boolean isAccessible
) {
}

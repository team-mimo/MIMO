package com.ssafy.mimo.domain.light.dto;

public record LightUpdateRequestDto(
	Long lightId,
	String nickname,
	String wakeupColor,
	String curColor,
	boolean isAccessible
) {
}

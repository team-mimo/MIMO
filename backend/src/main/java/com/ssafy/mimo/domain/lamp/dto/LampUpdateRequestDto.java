package com.ssafy.mimo.domain.lamp.dto;

public record LampUpdateRequestDto(
	Long lampId,
	String nickname,
	String wakeupColor,
	String curColor,
	boolean isAccessible
) {
}

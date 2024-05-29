package com.ssafy.mimo.domain.window.dto;

import lombok.Builder;

@Builder
public record WindowDetailResponseDto(
	Long windowId,
	String nickname,
	String macAddress,
	Integer openDegree,
	boolean isAccessible
) {
}

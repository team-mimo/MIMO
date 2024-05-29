package com.ssafy.mimo.domain.window.dto;

import lombok.Builder;

@Builder
public record WindowRegisterResponseDto(
	Long windowId,
	String nickname,
	String macAddress
) {
}

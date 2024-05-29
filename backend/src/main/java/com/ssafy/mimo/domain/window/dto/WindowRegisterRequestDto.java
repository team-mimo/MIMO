package com.ssafy.mimo.domain.window.dto;

import lombok.Builder;

@Builder
public record WindowRegisterRequestDto(
	Long hubId,
	String nickname,
	String macAddress
) {
}

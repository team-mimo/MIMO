package com.ssafy.mimo.domain.light.dto;

import lombok.Builder;

@Builder
public record LightRegisterResponseDto(
	Long lightId,
	String nickname,
	String macAddress
) {
}

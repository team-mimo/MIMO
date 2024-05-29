package com.ssafy.mimo.domain.lamp.dto;

import lombok.Builder;

@Builder
public record LampRegisterResponseDto(
	Long lampId,
	String nickname,
	String macAddress
) {
}

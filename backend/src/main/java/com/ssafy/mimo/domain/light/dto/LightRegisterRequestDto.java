package com.ssafy.mimo.domain.light.dto;

import lombok.Builder;

@Builder
public record LightRegisterRequestDto(
	Long hubId,
	String nickname,
	String macAddress
) {
}

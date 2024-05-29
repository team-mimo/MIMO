package com.ssafy.mimo.domain.lamp.dto;

import lombok.Builder;

@Builder
public record LampRegisterRequestDto(
	Long hubId,
	String nickname,
	String macAddress
) {
}

package com.ssafy.mimo.domain.curtain.dto;

import lombok.Builder;

@Builder
public record CurtainRegisterResponseDto(
	Long curtainId,
	String nickname,
	String macAddress
) {
}

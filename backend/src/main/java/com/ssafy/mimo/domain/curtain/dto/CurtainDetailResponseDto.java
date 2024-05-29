package com.ssafy.mimo.domain.curtain.dto;

import lombok.Builder;

@Builder
public record CurtainDetailResponseDto(
	Long curtainId,
	String nickname,
	String macAddress,
	Integer openDegree,
	boolean isAccessible
) {
}

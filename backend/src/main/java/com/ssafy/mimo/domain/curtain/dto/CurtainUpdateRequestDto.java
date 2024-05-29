package com.ssafy.mimo.domain.curtain.dto;

public record CurtainUpdateRequestDto(
	Long curtainId,
	String nickname,
	Integer openDegree,
	boolean isAccessible
) {
}

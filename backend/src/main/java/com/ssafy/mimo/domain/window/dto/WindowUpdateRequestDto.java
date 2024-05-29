package com.ssafy.mimo.domain.window.dto;

public record WindowUpdateRequestDto(
	Long windowId,
	String nickname,
	Integer openDegree,
	boolean isAccessible
) {
}

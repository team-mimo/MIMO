package com.ssafy.mimo.domain.curtain.dto;

import lombok.Builder;

@Builder
public record CurtainRegisterRequestDto(
	Long hubId,
	String nickname,
	String macAddress
) {}

package com.ssafy.mimo.user.dto;

import java.time.LocalTime;

import lombok.Builder;

@Builder
public record WakeupTimeDto(
	LocalTime wakeupTime
) {
}

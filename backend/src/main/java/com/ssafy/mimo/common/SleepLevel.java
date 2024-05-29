package com.ssafy.mimo.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SleepLevel {
	AWAKE(-1),
	AWAKENED(1),
	REM(6),
	LIGHT_SLEEP(4),
	DEEP_SLEEP(5);

	private final Integer value;
}

package com.ssafy.mimo.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceDefaults {
	LIGHT_WAKEUP_COLOR("FFFFFF"),
	LIGHT_CUR_COLOR("E0854D"),
	LAMP_WAKEUP_COLOR("FFFFFF"),
	LAMP_CUR_COLOR("E0854D"),
	CURTAIN_OPEN_DEGREE("100"),
	WINDOW_OPEN_DEGREE("100"),
	NIGHT_START_HOUR("18"),
	NIGHT_END_HOUR("3");

	private final String value;
}

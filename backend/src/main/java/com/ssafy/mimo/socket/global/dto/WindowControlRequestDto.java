package com.ssafy.mimo.socket.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WindowControlRequestDto {
	private String type;
	private Long windowId;
	@Builder.Default
	private WindowControlRequestDataDto data = new WindowControlRequestDataDto();
	@Override
	public String toString() {
		return "{\"type\":\"" + type +
				"\", \"windowId\":" + windowId +
				", \"data\":" + data.toString() +
				'}';
	}
}

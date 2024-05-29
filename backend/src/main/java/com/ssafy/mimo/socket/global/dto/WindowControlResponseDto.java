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
public class WindowControlResponseDto {
	private String type;
	private Long windowId;
	@Builder.Default
	private WindowControlResponseDataDto data = new WindowControlResponseDataDto();
	@Override
	public String toString() {
		return "{\"type\":\"" + type +
				"\", \"windowId\":" + windowId +
				", \"data\":" + data.toString() +
				'}';
	}

	public WindowControlResponseDto(WindowControlRequestDto windowControlRequestDto, WindowControlRequestDataDto windowControlRequestDataDto, String curStatus) {
		this.type = windowControlRequestDto.getType();
		this.windowId = windowControlRequestDto.getWindowId();
		this.data = new WindowControlResponseDataDto(windowControlRequestDataDto, curStatus);
	}
}

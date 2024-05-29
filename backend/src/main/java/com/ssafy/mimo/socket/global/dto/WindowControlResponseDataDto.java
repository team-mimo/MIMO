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
public class WindowControlResponseDataDto {
	@Builder.Default
	private String requestName = "request";
	@Builder.Default
	private String curStatus = "default";
	@Override
	public String toString() {
		return "{\"requestName\":\"" + requestName +
				"\", \"curStatus\":\"" + curStatus +
				"\"}";
	}

	public WindowControlResponseDataDto(WindowControlRequestDataDto windowControlRequestDataDto, String curStatus) {
		this.requestName = windowControlRequestDataDto.getRequestName();
		this.curStatus = curStatus;
	}
}

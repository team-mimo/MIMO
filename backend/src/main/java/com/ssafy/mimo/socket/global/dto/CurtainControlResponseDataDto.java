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
public class CurtainControlResponseDataDto {
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

	public CurtainControlResponseDataDto(CurtainControlRequestDataDto curtainControlRequestDataDto, String curStatus) {
		this.requestName = curtainControlRequestDataDto.getRequestName();
		this.curStatus = curStatus;
	}
}

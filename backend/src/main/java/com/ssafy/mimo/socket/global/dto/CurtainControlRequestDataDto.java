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
public class CurtainControlRequestDataDto {
	@Builder.Default
	private String requestName = "request";
	@Override
	public String toString() {
		return "{\"requestName\":\"" + requestName +
				"\"}";
	}
}

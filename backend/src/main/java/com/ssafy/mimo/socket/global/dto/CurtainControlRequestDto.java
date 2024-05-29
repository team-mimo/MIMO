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
public class CurtainControlRequestDto {
	private String type;
	private Long curtainId;
	@Builder.Default
	private CurtainControlRequestDataDto data = new CurtainControlRequestDataDto();
	@Override
	public String toString() {
		return "{\"type\":\"" + type +
				"\", \"curtainId\":" + curtainId +
				", \"data\":" + data.toString() +
				'}';
	}
}

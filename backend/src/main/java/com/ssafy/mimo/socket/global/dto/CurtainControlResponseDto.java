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
public class CurtainControlResponseDto {
	private String type;
	private Long curtainId;
	@Builder.Default
	private CurtainControlResponseDataDto data = new CurtainControlResponseDataDto();
	@Override
	public String toString() {
		return "{\"type\":\"" + type +
				"\", \"curtainId\":" + curtainId +
				", \"data\":" + data.toString() +
				'}';
	}

	public CurtainControlResponseDto(CurtainControlRequestDto curtainControlRequestDto, CurtainControlRequestDataDto curtainControlRequestDataDto, String curStatus) {
		this.type = curtainControlRequestDto.getType();
		this.curtainId = curtainControlRequestDto.getCurtainId();
		this.data = new CurtainControlResponseDataDto(curtainControlRequestDataDto, curStatus);
	}
}

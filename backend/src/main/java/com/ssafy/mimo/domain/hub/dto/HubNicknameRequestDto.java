package com.ssafy.mimo.domain.hub.dto;

import lombok.Builder;

@Builder
public record HubNicknameRequestDto(
       Long hubId,
       String nickname
) {}

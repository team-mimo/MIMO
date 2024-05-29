package com.ssafy.mimo.user.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.mimo.user.dto.KakaoUserInfoResponseDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class KakaoUserInfo { // 카카오 API를 이용해 토큰을 전송하여 유저 정보를 요청

	private final WebClient webClient;
	private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

	public KakaoUserInfoResponseDto getUserInfo(String token) {
		Flux<KakaoUserInfoResponseDto> response = webClient.get()
			.uri(USER_INFO_URI)
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.bodyToFlux(KakaoUserInfoResponseDto.class);
		return response.blockFirst();
	}
}
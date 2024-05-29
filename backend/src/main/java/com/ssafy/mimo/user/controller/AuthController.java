package com.ssafy.mimo.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.mimo.user.dto.LoginRequestDto;
import com.ssafy.mimo.user.dto.TokenResponseDto;
import com.ssafy.mimo.user.service.JwtTokenProvider;
import com.ssafy.mimo.user.service.KakaoAuthService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "로그인 컨트롤러", description = "로그인 및 회원가입 관련 기능이 포함되어 있음")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final UserService userService;
	private final KakaoAuthService kakaoAuthService;
	private final JwtTokenProvider jwtTokenProvider;

	// 카카오 로그인을 위해 회원가입 여부 확인, 이미 회원이면 Jwt 토큰 발급
	@Operation(summary = "카카오 로그인")
	@PostMapping("")
	public TokenResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
		Long userId = kakaoAuthService.isSignedUp(loginRequestDto.getAccessToken()); // 유저 고유번호 추출
		return new TokenResponseDto(jwtTokenProvider.createToken(userId.toString()));
	}
}

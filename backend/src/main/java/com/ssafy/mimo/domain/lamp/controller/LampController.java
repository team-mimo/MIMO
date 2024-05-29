package com.ssafy.mimo.domain.lamp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.mimo.domain.lamp.dto.LampDetailResponseDto;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterRequestDto;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterResponseDto;
import com.ssafy.mimo.domain.lamp.dto.LampUpdateRequestDto;
import com.ssafy.mimo.domain.lamp.service.LampService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "무드등 컨트롤러", description = "무드등 CRUD 및 제어 관련 기능이 포함되어 있음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lamp")
public class LampController {

	private final UserService userService;
	private final LampService lampService;

	@Operation(summary = "무드등 등록하기")
	@PostMapping
	public ResponseEntity<LampRegisterResponseDto> createLamp(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody LampRegisterRequestDto lampRegisterRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lampService.registerLamp(userId, lampRegisterRequestDto));
	}

	@Operation(summary = "해당 무드등 조회하기")
	@GetMapping("/{lampId}")
	public ResponseEntity<LampDetailResponseDto> readLamp(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long lampId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lampService.getLampDetail(userId, lampId));
	}

	@Operation(summary = "무드등 설정 수정하기")
	@PutMapping
	public ResponseEntity<String> updateLamp(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody LampUpdateRequestDto lampUpdateRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lampService.updateLamp(userId, lampUpdateRequestDto));
	}

	@Operation(summary = "무드등 등록 해제하기")
	@DeleteMapping("/{lampId}")
	public ResponseEntity<String> deleteLamp(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long lampId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lampService.unregisterLamp(userId, lampId));
	}

}

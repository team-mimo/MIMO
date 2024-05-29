package com.ssafy.mimo.domain.light.controller;

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

import com.ssafy.mimo.domain.light.dto.LightDetailResponseDto;
import com.ssafy.mimo.domain.light.dto.LightRegisterRequestDto;
import com.ssafy.mimo.domain.light.dto.LightRegisterResponseDto;
import com.ssafy.mimo.domain.light.dto.LightUpdateRequestDto;
import com.ssafy.mimo.domain.light.service.LightService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "조명 컨트롤러", description = "조명 CRUD 및 제어 관련 기능이 포함되어 있음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/light")
public class LightController {
	private final UserService userService;
	private final LightService lightService;

	@Operation(summary = "조명 등록하기")
	@PostMapping
	public ResponseEntity<LightRegisterResponseDto> createLight(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody LightRegisterRequestDto lightRegisterRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lightService.registerLight(userId, lightRegisterRequestDto));
	}

	@Operation(summary = "해당 조명 조회하기")
	@GetMapping("/{lightId}")
	public ResponseEntity<LightDetailResponseDto> readLight(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long lightId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lightService.getLightDetail(userId, lightId));
	}

	@Operation(summary = "조명 설정 수정하기")
	@PutMapping
	public ResponseEntity<String> updateLight(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody LightUpdateRequestDto lightUpdateRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lightService.updateLight(userId, lightUpdateRequestDto));
	}

	@Operation(summary = "조명 등록 해제하기")
	@DeleteMapping("/{lightId}")
	public ResponseEntity<String> deleteLight(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long lightId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(lightService.unregisterLight(userId, lightId));
	}
}

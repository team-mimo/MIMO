package com.ssafy.mimo.domain.window.controller;

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

import com.ssafy.mimo.domain.window.dto.WindowDetailResponseDto;
import com.ssafy.mimo.domain.window.dto.WindowRegisterRequestDto;
import com.ssafy.mimo.domain.window.dto.WindowRegisterResponseDto;
import com.ssafy.mimo.domain.window.dto.WindowUpdateRequestDto;
import com.ssafy.mimo.domain.window.service.WindowService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "창문 컨트롤러", description = "창문 CRUD 및 제어 관련 기능이 포함되어 있음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/window")
public class WindowController {

	private final UserService userService;
	private final WindowService windowService;

	@Operation(summary = "창문 등록하기")
	@PostMapping
	public ResponseEntity<WindowRegisterResponseDto> createWindow(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody WindowRegisterRequestDto windowRegisterRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(windowService.registerWindow(userId, windowRegisterRequestDto));
	}

	@Operation(summary = "해당 창문 조회하기")
	@GetMapping("/{windowId}")
	public ResponseEntity<WindowDetailResponseDto> readWindow(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long windowId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(windowService.getWindowDetail(userId, windowId));
	}

	@Operation(summary = "창문 설정 수정하기")
	@PutMapping
	public ResponseEntity<String> updateWindow(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody WindowUpdateRequestDto windowUpdateRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(windowService.updateWindow(userId, windowUpdateRequestDto));
	}

	@Operation(summary = "창문 등록 해제하기")
	@DeleteMapping("/{windowId}")
	public ResponseEntity<String> deleteWindow(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long windowId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(windowService.unregisterWindow(userId, windowId));
	}
}

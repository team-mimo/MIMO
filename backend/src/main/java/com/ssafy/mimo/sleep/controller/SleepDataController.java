package com.ssafy.mimo.sleep.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.mimo.sleep.dto.SleepDataDto;
import com.ssafy.mimo.sleep.service.SleepDataService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "수면 데이터 컨트롤러", description = "수면 데이터 및 수면 제어 관련 기능이 포함되어 있음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sleep-data")
public class SleepDataController {
	private final UserService userService;
	private final SleepDataService sleepDataService;

	@Operation(summary = "수면 데이터를 주기적으로 입력받아 저장 및 처리")
	@PostMapping
	public ResponseEntity<String> handleSleepData(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody SleepDataDto sleepDataDto) throws InterruptedException {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(sleepDataService.handleSleepData(userId, sleepDataDto));
	}

	@Operation(summary = "유저가 핸드폰 동작 시 알려주는 api")
	@PostMapping("/phone-on")
	public ResponseEntity<String> handlePhoneOn(
		@RequestHeader("X-AUTH-TOKEN") String token) throws InterruptedException {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(sleepDataService.handlePhoneOn(userId));
	}
}

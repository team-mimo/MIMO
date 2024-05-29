package com.ssafy.mimo.domain.curtain.controller;

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

import com.ssafy.mimo.domain.curtain.dto.CurtainDetailResponseDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterRequestDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterResponseDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainUpdateRequestDto;
import com.ssafy.mimo.domain.curtain.service.CurtainService;
import com.ssafy.mimo.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "커튼 컨트롤러", description = "커튼 CRUD 및 제어 관련 기능이 포함되어 있음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/curtain")
public class CurtainController {
	private final UserService userService;
	private final CurtainService curtainService;

	@Operation(summary = "커튼 등록하기")
	@PostMapping
	public ResponseEntity<CurtainRegisterResponseDto> createCurtain(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody CurtainRegisterRequestDto curtainRegisterRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(curtainService.registerCurtain(userId, curtainRegisterRequestDto));
	}

	@Operation(summary = "해당 커튼 조회하기")
	@GetMapping("/{curtainId}")
	public ResponseEntity<CurtainDetailResponseDto> readCurtain(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long curtainId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(curtainService.getCurtainDetail(userId, curtainId));
	}

	@Operation(summary = "커튼 설정 수정하기")
	@PutMapping
	public ResponseEntity<String> updateCurtain(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@RequestBody CurtainUpdateRequestDto curtainUpdateRequestDto) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(curtainService.updateCurtain(userId, curtainUpdateRequestDto));
	}

	@DeleteMapping("/{curtainId}")
	public ResponseEntity<String> deleteCurtain(
		@RequestHeader("X-AUTH-TOKEN") String token,
		@PathVariable Long curtainId) {
		Long userId = userService.getUserId(token);
		return ResponseEntity.ok(curtainService.unregisterCurtain(userId, curtainId));
	}
}

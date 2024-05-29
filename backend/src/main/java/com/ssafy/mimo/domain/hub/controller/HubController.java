package com.ssafy.mimo.domain.hub.controller;

import com.ssafy.mimo.domain.hub.dto.HubListResponseDto;
import com.ssafy.mimo.domain.hub.dto.HubNicknameRequestDto;
import com.ssafy.mimo.domain.hub.dto.HubRegisterRequestDto;
import com.ssafy.mimo.domain.hub.service.HubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "허브 컨트롤러", description = "허브 CRUD 관련 기능이 포함되어 있음")
@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {
    private final HubService hubService;

    @Operation(summary = "새 허브 출고")
    @GetMapping("/new")
    public ResponseEntity<String> releaseNewHub(
        @RequestHeader("X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(hubService.releaseHub());
    }

    @Operation(summary = "해당 집에 등록되어 있는 허브 리스트 조회")
    @GetMapping("/list")
    public ResponseEntity<List<HubListResponseDto>> getHubs(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @RequestParam Long houseId) {
        return ResponseEntity.ok(hubService.getHubs(houseId));
    }

    @Operation(summary = "집에 허브 등록")
    @PostMapping
    public ResponseEntity<String> registerHub(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @RequestBody HubRegisterRequestDto registerNewHubDto) {
        return ResponseEntity.ok(hubService.registerHub(registerNewHubDto.serialNumber(), registerNewHubDto.houseId()));
    }

    @Operation(summary = "집에서 허브 등록 해제")
    @DeleteMapping("/unregister")
    public ResponseEntity<String> unregisterHub(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @RequestParam Long hubId,
        @RequestParam Long houseId) {
        return ResponseEntity.ok(hubService.unregisterHub(hubId, houseId));
    }

    @Operation(summary = "허브 닉네임 변경")
    @PutMapping("/nickname")
    public ResponseEntity<String> updateHubNickname(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @RequestBody HubNicknameRequestDto hubNicknameDto) {
        return ResponseEntity.ok(hubService.updateHubNickname(hubNicknameDto.hubId(), hubNicknameDto.nickname()));
    }
}

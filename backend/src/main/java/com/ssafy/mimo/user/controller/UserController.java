package com.ssafy.mimo.user.controller;

import com.ssafy.mimo.user.dto.MyInfoResponseDto;
import com.ssafy.mimo.user.dto.WakeupTimeDto;
import com.ssafy.mimo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 컨트롤러", description = "유저 정보 확인 기능 및 기상시간 설정이 포함되어 있음")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "해당 사용자의 집/허브 유무 파악")
    @GetMapping("/myInfo")
    public ResponseEntity<MyInfoResponseDto> getHomeAndHubInfo(
        @RequestHeader("X-AUTH-TOKEN") String token) {
        Long userId = userService.getUserId(token);
        MyInfoResponseDto info = userService.getHomeAndHubInfo(userId);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "해당 유저의 설정된 기상시간 가져오기")
    @GetMapping("/wakeup-time")
    public ResponseEntity<WakeupTimeDto> getWakeupTime(
        @RequestHeader("X-AUTH-TOKEN") String token) {
        Long userId = userService.getUserId(token);
        return ResponseEntity.ok(userService.getWakeupTime(userId));
    }

    @Operation(summary = "해당 유저의 기상시간 설정하기 -> \"07:00:00\" 의 형태로 보내면 됩니다! 아래 example 은 무시하세요!")
    @PutMapping("/wakeup-time")
    public ResponseEntity<WakeupTimeDto> setWakeupTime(
        @RequestHeader("X-AUTH-TOKEN") String token,
        @RequestBody WakeupTimeDto wakeupTimeDto) {
        Long userId = userService.getUserId(token);
        return ResponseEntity.ok(userService.setWakeupTime(userId, wakeupTimeDto));
    }

    @Operation(summary = "해당 유저의 기상시간 해제하기")
    @DeleteMapping("/wakeup-time")
    public ResponseEntity<String> deleteWakeupTime(
        @RequestHeader("X-AUTH-TOKEN") String token) {
        Long userId = userService.getUserId(token);
        return ResponseEntity.ok(userService.deleteWakeupTime(userId));
    }
}

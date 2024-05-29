package com.ssafy.mimo.domain.house.controller;

import com.ssafy.mimo.domain.house.dto.*;
import com.ssafy.mimo.domain.house.service.HouseService;
import com.ssafy.mimo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "집 컨트롤러", description = "집 CRUD 관련 기능이 포함되어 있음")
@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
public class HouseController {

	private final HouseService houseService;
	private final UserService userService;

	@Operation(summary = "해당 사용자에 등록되어 있는 집 리스트 조회")
	@GetMapping
	public ResponseEntity<List<HouseResponseDto>> getHouses(@RequestHeader("X-AUTH-TOKEN") String token) {
		Long userId = userService.getUserId(token);
		List<HouseResponseDto> houses = houseService.getHouses(userId);
		return ResponseEntity.ok(houses);
	}

	@Operation(summary = "허브가 없는 집 등록")
	@PostMapping("/new")
	public ResponseEntity<NewHouseResponseDto> registerNewHouse(@RequestHeader("X-AUTH-TOKEN") String token,
																@RequestBody NewHouseRequestDto newHouseRequestDto) {
		Long userId = userService.getUserId(token);
		NewHouseResponseDto newResponse = houseService.registerNewHouse(userId, newHouseRequestDto);
		return ResponseEntity.ok(newResponse);
	}

	@Operation(summary = "허브가 이미 등록된 집 등록")
	@PostMapping
	public ResponseEntity<OldHouseResponseDto> registerOldHouse(@RequestHeader("X-AUTH-TOKEN") String token,
																@RequestBody OldHouseRequestDto oldHouseRequestDto) {
		Long userId = userService.getUserId(token);
		OldHouseResponseDto oldResponse = houseService.registerOldHouse(userId, oldHouseRequestDto);
		return ResponseEntity.ok(oldResponse);
	}

	@Operation(summary = "집 등록 해제")
	@DeleteMapping("/{houseId}")
	public ResponseEntity<Void> unregisterHouse(@RequestHeader("X-AUTH-TOKEN") String token,
												@PathVariable("houseId") Long houseId) {
		Long userId = userService.getUserId(token);
		houseService.unregisterHouse(userId, houseId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "집 별칭 변경")
	@PutMapping("/{houseId}")
	public ResponseEntity<String> updateHouseNickname(@RequestHeader("X-AUTH-TOKEN") String token,
													  @PathVariable("houseId") Long houseId,
													  @RequestBody HouseNicknameRequestDto houseNicknameRequestDto) {
		Long userId = userService.getUserId(token);
		houseService.updateHouseNickname(userId, houseId, houseNicknameRequestDto);
		return ResponseEntity.ok(houseNicknameRequestDto.nickname());
	}

	@Operation(summary = "현재 거주지 변경")
	@PutMapping("/{houseId}/home")
	public ResponseEntity<String> updateHouseStatus(@RequestHeader("X-AUTH-TOKEN") String token,
													@PathVariable("houseId") Long houseId) {
		Long userId = userService.getUserId(token);
		boolean isHome = houseService.updateHouseStatus(userId, houseId);
		return isHome ?
				ResponseEntity.ok().body("{\"is_home\": \"true\"}") :
				ResponseEntity.ok().body("{\"is_home\": \"false\"}");
	}

	@Operation(summary = "해당 집에 등록되어 있는 기기 리스트 조회")
	@GetMapping("/{houseId}/devices")
	public ResponseEntity<HouseDeviceResponseDto> getDevices(@RequestHeader("X-AUTH-TOKEN") String token,
															 @PathVariable("houseId") Long houseId) throws InterruptedException {
		Long userId = userService.getUserId(token);
		HouseDeviceResponseDto devices = houseService.getDevices(userId, houseId);
		return ResponseEntity.ok(devices);
	}
}

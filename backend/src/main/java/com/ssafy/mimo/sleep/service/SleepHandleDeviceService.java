package com.ssafy.mimo.sleep.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDataDto;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDto;
import com.ssafy.mimo.domain.house.dto.DeviceDetailDto;
import com.ssafy.mimo.domain.house.dto.DeviceListDto;
import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.house.entity.UserHouse;
import com.ssafy.mimo.domain.house.service.HouseService;
import com.ssafy.mimo.sleep.dto.SleepDataDto;
import com.ssafy.mimo.sleep.entity.SleepData;
import com.ssafy.mimo.sleep.repository.SleepDataRepository;
import com.ssafy.mimo.socket.global.SocketController;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ssafy.mimo.common.SleepLevel.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepHandleDeviceService {
	private final UserService userService;
	private final HouseService houseService;
	private final DeviceHandlerService deviceHandlerService;
	private final SleepDataRepository sleepDataRepository;
	private final SocketController socketController;

	public void handleDeviceBySleepLevel(Long userId, SleepDataDto sleepDataDto) throws InterruptedException {
		Integer sleepLevel = sleepDataDto.sleepLevel();
		User user = userService.findUserById(userId);

		// 유저의 현재 집에 연결된 모든 기기 불러오기
		List<DeviceListDto> devices = findDevicesAtHome(userId, user);

		// 잠에 들면 동작하는 기기 제어
		if (sleepLevel == LIGHT_SLEEP.getValue()) {
			devices.stream()
				.filter(device -> device.userId().equals(userId))
				.forEach(deviceHandlerService::handleOnSleep);
			return;
		}

		// // 완전히 깨어나면 동작하는 기기 제어
		// SleepData priorSleepData = sleepDataRepository.findTopByUserIdOrderByCreatedDttmDesc(userId);
		// if (sleepLevel == AWAKE.getValue() && priorSleepData.getSleepLevel() >= LIGHT_SLEEP.getValue()) {
		// 	devices.stream()
		// 		.filter(device -> device.userId().equals(userId))
		// 		.forEach(deviceHandlerService::handleOnWakeUp);
		// 	return;
		// }

		// 아침 렘 수면 동작 들어서면 동작하는 기기 제어
		if (sleepLevel == REM.getValue()) {
			devices.stream()
				.filter(device -> device.userId().equals(userId))
				.forEach(device -> deviceHandlerService.handleOnRem(userId, device));
		}
	}

	// 아침 기상 시 핸드폰 동작하면 기상 로직 실행
	public void handleDeviceWakeup(Long userId) throws InterruptedException {
		User user = userService.findUserById(userId);

		// 유저의 현재 집에 연결된 모든 기기 불러오기
		List<DeviceListDto> devices = findDevicesAtHome(userId, user);
		devices.stream()
			.filter(device -> device.userId().equals(userId))
			.forEach(deviceHandlerService::handleOnWakeUp);

		// 유저의 기상상태 저장하기
		SleepData sleepData = SleepData.builder()
			.user(user)
			.sleepLevel(AWAKE.getValue())
			.build();
		sleepDataRepository.save(sleepData);

	}

	// 밤에 핸드폰 동작 시 실행되는 메서드 (무드등 켜주기)
	public void handleDeviceNightPhone(Long userId) throws InterruptedException {
		User user = userService.findUserById(userId);

		// 유저의 현재 집에 연결된 모든 기기 불러오기
		List<DeviceDetailDto> devices = findDevicesStateAtHome(userId, user);

		// 집의 조명이 다 꺼지지 않은 상황이라면 return
		Boolean isAllLightOff = checkAllLightOff(userId, devices);
		if (!isAllLightOff) {
			return;
		}

		devices.stream()
			.filter(device -> device.userId().equals(userId))
			.forEach(deviceHandlerService::handleLampOn);
	}


	// 현재 집에 연결된 모든 기기 불러오는 메서드
	private List<DeviceListDto> findDevicesAtHome(Long userId, User user) throws InterruptedException {
		// 유저에 연결된 house 중 현재 집 id 찾기
		Long houseId = user.getUserHouse().stream()
			.filter(UserHouse::isHome)
			.findFirst()
			.map(UserHouse::getHouse)
			.map(House::getId)
			.orElseThrow(() -> new IllegalArgumentException("현재 집으로 설정된 집이 없습니다."));

		// 유저의 현재 집에 연결된 모든 기기 불러오기
		List<DeviceListDto> devices = houseService.getDeviceList(userId, houseId).devices();

		return devices;
	}

	// 현재 집에 연결된 모든 기기 상태값과 함께 불러오는 메서드
	private List<DeviceDetailDto> findDevicesStateAtHome(Long userId, User user) throws InterruptedException {
		// 유저에 연결된 house 중 현재 집 id 찾기
		Long houseId = user.getUserHouse().stream()
			.filter(UserHouse::isHome)
			.findFirst()
			.map(UserHouse::getHouse)
			.map(House::getId)
			.orElseThrow(() -> new IllegalArgumentException("현재 집으로 설정된 집이 없습니다."));

		// 유저의 현재 집에 연결된 모든 기기 불러오기
		List<DeviceDetailDto> devices = houseService.getDevices(userId, houseId).devices();

		return devices;
	}


	// 현재 집의 모든 기기 중 내 조명 및 무드등이 전부 꺼져있는지 확인하는 메서드
	private Boolean checkAllLightOff(Long userId, List<DeviceDetailDto> devices) {
		User user = userService.findUserById(userId);

		// devices 를 순회하면서 type 이 "light" 혹은 "lamp" 인 기기들의 현재 상태를 확인하고, 모든 조명이 꺼져있으면 trie 반환, 만일 한번이라고 null 이 나오면 중지하고 null 반환
		for (DeviceDetailDto device : devices) {
			// 기기의 유저가 현재 유저와 같지 않으면 continue
			if (!device.userId().equals(userId)) {
				continue;
			}
			// 기기의 타입이 "light" 혹은 "lamp" 가 아니면 continue
			if (!device.type().equals("light") && !device.type().equals("lamp")) {
				continue;
			}

			// 기기의 현재 상태 확인
			Boolean isDeviceOff = checkDeviceOff(device);

			// 한 기기라도 null 값이 반환되면 false 반환
			if (isDeviceOff == null) { return false; }

			// 한 기기라도 켜져있으면 false 반환
			if (!isDeviceOff) { return false; }
		}
		return true;
	}

	// 해당 기기의 현재 상태를 확인하는 메서드
	private Boolean checkDeviceOff(DeviceDetailDto device) {
		ManualControlRequestDto manualControlRequestDto = ManualControlRequestDto.builder()
			.type(device.type())
			.deviceId(device.deviceId())
			.data(ManualControlRequestDataDto.builder()
				.requestName("getState")
				.build())
			.build();
		String message = manualControlRequestDto.toString();

		// 상태 확인 요청 보내기
		String requestId = SocketController.sendMessage(device.hubId(), message);
		if (requestId == null) {
			return false;
		}

		// 상태 응답 받기
		try {
			String responseMessage = SocketController.getMessage(device.hubId(), requestId);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode responseJson = objectMapper.readTree(responseMessage);
			int state = responseJson.get("data").get("state").asInt();
			return state == 0;
		} catch (JsonProcessingException e) {
			System.out.println("Error parsing the message: " + e.getMessage());
			return false;
		}
	}
}

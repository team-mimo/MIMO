package com.ssafy.mimo.sleep.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;
import static com.ssafy.mimo.common.SleepLevel.*;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.sleep.dto.SleepDataDto;
import com.ssafy.mimo.sleep.entity.SleepData;
import com.ssafy.mimo.sleep.repository.SleepDataRepository;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SleepDataService {
	private final SleepDataRepository sleepDataRepository;
	private final UserService userService;
	private final SleepHandleDeviceService sleepHandleDeviceService;

	public String handleSleepData(Long userId, SleepDataDto sleepDataDto) throws InterruptedException {
		// 수면 데이터에 따라 IoT 기기를 제어
		sleepHandleDeviceService.handleDeviceBySleepLevel(userId, sleepDataDto);

		// 들어온 수면 데이터를 저장
		saveSleepData(userId, sleepDataDto);

		return "수면 데이터가 성공적으로 전송되었습니다.";
	}

	// 수면 데이터를 저장하는 메서드
	private void saveSleepData(Long userId, SleepDataDto sleepDataDto) {
		User user = userService.findUserById(userId);

		SleepData sleepData = SleepData.builder()
			.user(user)
			.sleepLevel(sleepDataDto.sleepLevel())
			.build();
		sleepDataRepository.save(sleepData);
	}

	// 핸드폰 동작 시 동작하는 메서드
	public String handlePhoneOn(Long userId) throws InterruptedException {
		User user = userService.findUserById(userId);
		Integer sleepLevel = sleepDataRepository.findTopByUserIdOrderByCreatedDttmDesc(userId).getSleepLevel();
		 // 해당 유저의 가장 최근 수면데이터가 자고 있던 상태였고, 기상설정 시간 1시간 전후라면, handleOnWakeup 메서드 호출
		if (sleepLevel >= LIGHT_SLEEP.getValue() && isWakeupTime(user)) {
			sleepHandleDeviceService.handleDeviceWakeup(userId);
			return "정상적으로 전송되었습니다.";
		}

		// 저녁 시간이고, 현재 집의 해당 유저의 모든 조명기기가 꺼져 있는 상태라면 무드등을 켜주기
		if (isNightTime()) {
			sleepHandleDeviceService.handleDeviceNightPhone(userId);
		}

		return "정상적으로 전송되었습니다.";
	}

	private boolean isWakeupTime(User user) {
		LocalTime wakeupTime = user.getWakeupTime();
		LocalTime currentTime = LocalTime.now();
		// 지금 시간이 기상 시간 1시간 전후라면 true 반환
		return currentTime.isAfter(wakeupTime.minusHours(1)) && currentTime.isBefore(wakeupTime.plusHours(1));
	}

	private  boolean isNightTime() {
		LocalTime currentTime = LocalTime.now();
		// 18시 이후 부터 03시 이전까지인 경우가 디폴트
		return currentTime.isAfter(LocalTime.of(Integer.parseInt(NIGHT_START_HOUR.getValue()), 0)) || currentTime.isBefore(LocalTime.of(Integer.parseInt(NIGHT_END_HOUR.getValue()), 0));
	}
}

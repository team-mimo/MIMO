package com.ssafy.mimo.domain.lamp.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.service.HubService;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterRequestDto;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterResponseDto;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.lamp.repository.LampRepository;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LampRegisterService {
	private final LampRepository lampRepository;
	private final UserService userService;
	private final HubService hubService;

	public LampRegisterResponseDto registerNewLamp(Long userId, LampRegisterRequestDto lampRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(lampRegisterRequestDto.hubId());

		// 램프 객체 생성 및 저장
		Lamp lamp = Lamp.builder()
			.user(user)
			.hub(hub)
			.registeredDttm(LocalDateTime.now())
			.isRegistered(true)
			.isAccessible(true)
			.nickname(lampRegisterRequestDto.nickname())
			.macAddress(lampRegisterRequestDto.macAddress())
			.wakeupColor(LAMP_WAKEUP_COLOR.getValue())
			.curColor(LAMP_CUR_COLOR.getValue())
			.build();
		lampRepository.save(lamp);

		return LampRegisterResponseDto.builder()
			.lampId(lamp.getId())
			.nickname(lamp.getNickname())
			.macAddress(lamp.getMacAddress())
			.build();
	}

	public LampRegisterResponseDto registerOldLamp(Long userId, Lamp lamp, LampRegisterRequestDto lampRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(lampRegisterRequestDto.hubId());

		// 램프 객체 수정 및 저장
		lamp.setUser(user);
		lamp.setHub(hub);
		lamp.setRegistered(true);
		lamp.setRegisteredDttm(LocalDateTime.now());
		lamp.setUnregisteredDttm(null);
		lamp.setWakeupColor(LAMP_WAKEUP_COLOR.getValue());
		lamp.setCurColor(LAMP_CUR_COLOR.getValue());
		lamp.setAccessible(true);
		lamp.setNickname(lampRegisterRequestDto.nickname());
		lampRepository.save(lamp);

		return LampRegisterResponseDto.builder()
			.lampId(lamp.getId())
			.nickname(lamp.getNickname())
			.macAddress(lamp.getMacAddress())
			.build();
	}
}

package com.ssafy.mimo.domain.light.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.service.HubService;
import com.ssafy.mimo.domain.light.dto.LightRegisterRequestDto;
import com.ssafy.mimo.domain.light.dto.LightRegisterResponseDto;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.light.repository.LightRepository;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LightRegisterService {
	private final LightRepository lightRepository;
	private final UserService userService;
	private final HubService hubService;

	public LightRegisterResponseDto registerNewLight(Long userId, LightRegisterRequestDto lightRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(lightRegisterRequestDto.hubId());

		// 조명 객체 생성 및 저장
		Light light = Light.builder()
			.user(user)
			.hub(hub)
			.registeredDttm(LocalDateTime.now())
			.isRegistered(true)
			.isAccessible(true)
			.nickname(lightRegisterRequestDto.nickname())
			.macAddress(lightRegisterRequestDto.macAddress())
			.wakeupColor(LIGHT_WAKEUP_COLOR.getValue())
			.curColor(LIGHT_CUR_COLOR.getValue())
			.build();
		lightRepository.save(light);

		return LightRegisterResponseDto.builder()
			.lightId(light.getId())
			.nickname(light.getNickname())
			.macAddress(light.getMacAddress())
			.build();
	}

	public LightRegisterResponseDto registerOldLight(Long userId, Light light,
		LightRegisterRequestDto lightRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(lightRegisterRequestDto.hubId());

		// 조명 객체 수정 및 저장
		light.setUser(user);
		light.setHub(hub);
		light.setRegistered(true);
		light.setRegisteredDttm(LocalDateTime.now());
		light.setUnregisteredDttm(null);
		light.setWakeupColor(LIGHT_WAKEUP_COLOR.getValue());
		light.setCurColor(LIGHT_CUR_COLOR.getValue());
		light.setAccessible(true);
		light.setNickname(lightRegisterRequestDto.nickname());
		lightRepository.save(light);

		return LightRegisterResponseDto.builder()
			.lightId(light.getId())
			.nickname(light.getNickname())
			.macAddress(light.getMacAddress())
			.build();
	}
}

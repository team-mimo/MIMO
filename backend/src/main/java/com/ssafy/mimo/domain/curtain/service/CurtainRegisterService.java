package com.ssafy.mimo.domain.curtain.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterRequestDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterResponseDto;
import com.ssafy.mimo.domain.curtain.entity.Curtain;
import com.ssafy.mimo.domain.curtain.repository.CurtainRepository;
import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.service.HubService;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CurtainRegisterService {
	private final CurtainRepository curtainRepository;
	private final UserService userService;
	private final HubService hubService;

	public CurtainRegisterResponseDto registerNewCurtain(Long userId, CurtainRegisterRequestDto curtainRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(curtainRegisterRequestDto.hubId());

		// 커튼 객체 생성 및 저장
		Curtain curtain = Curtain.builder()
			.user(user)
			.hub(hub)
			.registeredDttm(LocalDateTime.now())
			.isRegistered(true)
			.isAccessible(true)
			.nickname(curtainRegisterRequestDto.nickname())
			.macAddress(curtainRegisterRequestDto.macAddress())
			.openDegree(Integer.valueOf(CURTAIN_OPEN_DEGREE.getValue()))
			.build();
		curtainRepository.save(curtain);

		return CurtainRegisterResponseDto.builder()
			.curtainId(curtain.getId())
			.nickname(curtain.getNickname())
			.macAddress(curtain.getMacAddress())
			.build();
	}

	public CurtainRegisterResponseDto registerOldCurtain(Long userId, Curtain curtain, CurtainRegisterRequestDto curtainRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(curtainRegisterRequestDto.hubId());

		// 커튼 객체 수정 및 저장
		curtain.setUser(user);
		curtain.setHub(hub);
		curtain.setRegistered(true);
		curtain.setRegisteredDttm(LocalDateTime.now());
		curtain.setUnregisteredDttm(null);
		curtain.setOpenDegree(Integer.valueOf(CURTAIN_OPEN_DEGREE.getValue()));
		curtain.setAccessible(true);
		curtain.setNickname(curtainRegisterRequestDto.nickname());
		curtainRepository.save(curtain);

		return CurtainRegisterResponseDto.builder()
			.curtainId(curtain.getId())
			.nickname(curtain.getNickname())
			.macAddress(curtain.getMacAddress())
			.build();
	}
}

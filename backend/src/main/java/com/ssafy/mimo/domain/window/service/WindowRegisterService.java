package com.ssafy.mimo.domain.window.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.service.HubService;
import com.ssafy.mimo.domain.window.dto.WindowRegisterRequestDto;
import com.ssafy.mimo.domain.window.dto.WindowRegisterResponseDto;
import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import com.ssafy.mimo.domain.window.repository.WindowRepository;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WindowRegisterService {
	private final WindowRepository windowRepository;
	private final UserService userService;
	private final HubService hubService;

	public WindowRegisterResponseDto registerNewWindow(Long userId, WindowRegisterRequestDto windowRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(windowRegisterRequestDto.hubId());

		// 창문 객체 생성 및 저장
		SlidingWindow slidingWindow = SlidingWindow.builder()
			.user(user)
			.hub(hub)
			.registeredDttm(LocalDateTime.now())
			.isRegistered(true)
			.isAccessible(true)
			.nickname(windowRegisterRequestDto.nickname())
			.macAddress(windowRegisterRequestDto.macAddress())
			.openDegree(Integer.valueOf(WINDOW_OPEN_DEGREE.getValue()))
			.build();
		windowRepository.save(slidingWindow);

		return WindowRegisterResponseDto.builder()
			.windowId(slidingWindow.getId())
			.nickname(slidingWindow.getNickname())
			.macAddress(slidingWindow.getMacAddress())
			.build();
	}

	public WindowRegisterResponseDto registerOldWindow(Long userId, SlidingWindow slidingWindow,
		WindowRegisterRequestDto windowRegisterRequestDto) {
		// 유저와 허브 객체 찾기
		User user = userService.findUserById(userId);
		Hub hub = hubService.findHubById(windowRegisterRequestDto.hubId());

		// 창문 객체 수정 및 저장
		slidingWindow.setUser(user);
		slidingWindow.setHub(hub);
		slidingWindow.setRegistered(true);
		slidingWindow.setRegisteredDttm(LocalDateTime.now());
		slidingWindow.setUnregisteredDttm(null);
		slidingWindow.setOpenDegree(Integer.valueOf(WINDOW_OPEN_DEGREE.getValue()));
		slidingWindow.setAccessible(true);
		slidingWindow.setNickname(windowRegisterRequestDto.nickname());
		windowRepository.save(slidingWindow);

		return WindowRegisterResponseDto.builder()
			.windowId(slidingWindow.getId())
			.nickname(slidingWindow.getNickname())
			.macAddress(slidingWindow.getMacAddress())
			.build();
	}
}

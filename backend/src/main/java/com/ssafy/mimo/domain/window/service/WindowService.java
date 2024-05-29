package com.ssafy.mimo.domain.window.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.window.dto.WindowDetailResponseDto;
import com.ssafy.mimo.domain.window.dto.WindowRegisterRequestDto;
import com.ssafy.mimo.domain.window.dto.WindowRegisterResponseDto;
import com.ssafy.mimo.domain.window.dto.WindowUpdateRequestDto;
import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import com.ssafy.mimo.domain.window.repository.WindowRepository;
import com.ssafy.mimo.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WindowService {
	private final WindowRepository windowRepository;
	private final WindowRegisterService windowRegisterService;

	// 창문 등록하는 메서드
	public WindowRegisterResponseDto registerWindow(Long userId, WindowRegisterRequestDto windowRegisterRequestDto) {
		String macAddress = windowRegisterRequestDto.macAddress();

		// db 에 없는 신규 기기의 경우
		if (windowRepository.findByMacAddress(macAddress).isEmpty()) {
			return windowRegisterService.registerNewWindow(userId, windowRegisterRequestDto);
		}

		// 기존에 db 에 있는 경우 해당 무드등 불러오기
		SlidingWindow slidingWindow = findWindowByMacAddress(macAddress);

		// 등록 해제 되어 있는 기기인 경우 다시 등록 절차
		if (!slidingWindow.isRegistered()) {
			return windowRegisterService.registerOldWindow(userId, slidingWindow, windowRegisterRequestDto);
		}

		// 이미 등록된 기기인 경우 오류 안내
		throw new IllegalArgumentException("이미 등록된 창문입니다.");
	}

	;

	// 창문 등록해제 하는 메서드
	public String unregisterWindow(Long userId, Long windowId) {
		SlidingWindow slidingWindow = findWindowById(windowId);

		// 이미 등록 해제된 경우
		if (!slidingWindow.isRegistered()) {
			throw new IllegalArgumentException("이미 등록 해제된 창문입니다.");
		}

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(slidingWindow.getUser(), userId);

		// 창문 객체 수정 및 저장
		slidingWindow.setUser(null);
		slidingWindow.setHub(null);
		slidingWindow.setUnregisteredDttm(LocalDateTime.now());
		slidingWindow.setRegistered(false);
		windowRepository.save(slidingWindow);

		return "창문이 등록 해제되었습니다.";
	}

	// 해당 창문 불러오는 메서드
	public WindowDetailResponseDto getWindowDetail(Long userId, Long windowId) {
		SlidingWindow slidingWindow = findWindowById(windowId);

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(slidingWindow.getUser(), userId);

		return WindowDetailResponseDto.builder()
			.windowId(slidingWindow.getId())
			.nickname(slidingWindow.getNickname())
			.macAddress(slidingWindow.getMacAddress())
			.build();
	}

	// 창문 설정 수정하는 메서드
	public String updateWindow(Long userId, WindowUpdateRequestDto windowUpdateRequestDto) {
		SlidingWindow slidingWindow = findWindowById(windowUpdateRequestDto.windowId());

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(slidingWindow.getUser(), userId);

		// 창문 객체 수정 및 저장
		slidingWindow.setNickname(windowUpdateRequestDto.nickname());
		slidingWindow.setOpenDegree(windowUpdateRequestDto.openDegree());
		slidingWindow.setAccessible(windowUpdateRequestDto.isAccessible());
		windowRepository.save(slidingWindow);

		return "창문 설정이 업데이트 되었습니다.";
	}

	// 창문 mac 주소로 조명을 찾는 메서드
	public SlidingWindow findWindowByMacAddress(String macAddress) {
		return windowRepository.findByMacAddress(macAddress)
			.orElseThrow(() -> new IllegalArgumentException("해당 MAC 주소를 가진 창문이 존재하지 않습니다."));
	}

	// 창문 id 로 창문 찾는 메서드
	public SlidingWindow findWindowById(Long windowId) {
		return windowRepository.findById(windowId)
			.orElseThrow(() -> new IllegalArgumentException("해당 창문이 존재하지 않습니다."));
	}

	// 기기 주인인지 확인
	public void checkUserAuthority(User user, Long userId) {
		if (!user.getId().equals(userId)) {
			throw new IllegalArgumentException("권한이 없는 사용자입니다.");
		}
	}
}

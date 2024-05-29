package com.ssafy.mimo.domain.lamp.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.lamp.dto.LampDetailResponseDto;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterRequestDto;
import com.ssafy.mimo.domain.lamp.dto.LampRegisterResponseDto;
import com.ssafy.mimo.domain.lamp.dto.LampUpdateRequestDto;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.lamp.repository.LampRepository;
import com.ssafy.mimo.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LampService {
	private final LampRepository lampRepository;
	private final LampRegisterService lampRegisterService;

	// 무드등 등록하는 메서드
	public LampRegisterResponseDto registerLamp(Long userId, LampRegisterRequestDto lampRegisterRequestDto) {
		String macAddress = lampRegisterRequestDto.macAddress();

		// db 에 없는 신규 기기인 경우
		if (lampRepository.findByMacAddress(macAddress).isEmpty()) {
			return lampRegisterService.registerNewLamp(userId, lampRegisterRequestDto);
		}

		// 기존에 db에 있는 경우 해당 무드등 불러오기
		Lamp lamp = findLampByMacAddress(macAddress);

		// 등록해제 되어 있는 기기인 경우 다시 등록 절차
		if (!lamp.isRegistered()) {
			return lampRegisterService.registerOldLamp(userId, lamp, lampRegisterRequestDto);
		}

		// 이미 등록된 기기인 경우 오류 안내
		throw new IllegalArgumentException("이미 등록된 무드등입니다.");
	}

	// 무드등 등록해제하는 메서드
	public String unregisterLamp(Long userId, Long lampId) {
		Lamp lamp = findLampById(lampId);

		// 이미 등록 해제된 경우
		if (!lamp.isRegistered()) {
			throw new IllegalArgumentException("이미 등록 해제된 무드등입니다.");
		}

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(lamp.getUser(), userId);

		// 무드등 객체 수정 및 저장
		lamp.setUser(null);
		lamp.setHub(null);
		lamp.setUnregisteredDttm(LocalDateTime.now());
		lamp.setRegistered(false);
		lampRepository.save(lamp);

		return "무드등이 등록 해제되었습니다.";
	}

	// 해당 무드등 불러오는 메서드
	public LampDetailResponseDto getLampDetail(Long userId, Long lampId) {
		Lamp lamp = findLampById(lampId);

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(lamp.getUser(), userId);

		return LampDetailResponseDto.builder()
			.lampId(lamp.getId())
			.nickname(lamp.getNickname())
			.wakeupColor(lamp.getWakeupColor())
			.curColor(lamp.getCurColor())
			.macAddress(lamp.getMacAddress())
			.isAccessible(lamp.isAccessible())
			.build();
	}

	// 무드등 설정 업데이트 하는 메서드
	public String updateLamp(Long userId, LampUpdateRequestDto lampUpdateRequestDto) {
		Lamp lamp = findLampById(lampUpdateRequestDto.lampId());

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(lamp.getUser(), userId);

		lamp.setNickname(lampUpdateRequestDto.nickname());
		lamp.setWakeupColor(lampUpdateRequestDto.wakeupColor());
		lamp.setCurColor(lampUpdateRequestDto.curColor());
		lamp.setAccessible(lampUpdateRequestDto.isAccessible());
		lampRepository.save(lamp);

		return "무드등 설정이 업데이트 되었습니다.";
	}

	// 조명 mac 주소로 조명을 찾는 메서드
	public Lamp findLampByMacAddress(String macAddress) {
		return lampRepository.findByMacAddress(macAddress)
			.orElseThrow(() -> new IllegalArgumentException("해당 MAC 주소를 가진 무드등이 존재하지 않습니다."));
	}

	// 무드등 id 로 무드등 찾는 메서드
	public Lamp findLampById(Long lampId) {
		return lampRepository.findById(lampId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 무드등이 존재하지 않습니다."));
	}

	// 기기 주인인지 확인
	public void checkUserAuthority(User user, Long userId) {
		if (!user.getId().equals(userId)) {
			throw new IllegalArgumentException("권한이 없는 사용자입니다.");
		}
	}

	// 무드등  현재 색상 받아서 저장하는 메서드
	public void setLampCurColor(Long lampId, String curColor) {
		Lamp lamp = findLampById(lampId);
		lamp.setCurColor(curColor);
		lampRepository.save(lamp);
	}

	// 무드등 현재 색상 가져오는 메서드
	public String getLampCurColor(Long lampId) {
		Lamp lamp = findLampById(lampId);
		return lamp.getCurColor();
	}
}

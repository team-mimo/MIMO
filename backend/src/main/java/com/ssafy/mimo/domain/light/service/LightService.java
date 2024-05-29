package com.ssafy.mimo.domain.light.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.light.dto.LightDetailResponseDto;
import com.ssafy.mimo.domain.light.dto.LightRegisterRequestDto;
import com.ssafy.mimo.domain.light.dto.LightRegisterResponseDto;
import com.ssafy.mimo.domain.light.dto.LightUpdateRequestDto;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.light.repository.LightRepository;
import com.ssafy.mimo.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LightService {
	private final LightRepository lightRepository;
	private final LightRegisterService lightRegisterService;

	// 조명 등록하는 메서드
	public LightRegisterResponseDto registerLight(Long userId, LightRegisterRequestDto lightRegisterRequestDto) {
		String macAddress = lightRegisterRequestDto.macAddress();

		// db 에 없는 신규 기기인 경우
		if (lightRepository.findByMacAddress(macAddress).isEmpty()) {
			return lightRegisterService.registerNewLight(userId, lightRegisterRequestDto);
		}

		// 기존에 db에 있는 경우 해당 조명 불러오기
		Light light = findLightByMacAddress(macAddress);

		// 등록해제 되어 있는 기기인 경우 다시 등록 절차
		if (!light.isRegistered()) {
			return lightRegisterService.registerOldLight(userId, light, lightRegisterRequestDto);
		}

		// 이미 등록된 기기인 경우 오류 안내
		throw new IllegalArgumentException("이미 등록된 조명입니다.");
	}

	// 조명 등록 해제하는 메서드
	public String unregisterLight(Long userId, Long lightId) {
		Light light = findLightById(lightId);

		if (!light.isRegistered()) {
			throw new IllegalArgumentException("이미 등록 해제된 조명입니다.");
		}

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(light.getUser(), userId);

		// 조명 객체 수정 및 저장
		light.setUser(null);
		light.setHub(null);
		light.setRegistered(false);
		light.setUnregisteredDttm(LocalDateTime.now());
		lightRepository.save(light);

		return "조명이 등록 해제되었습니다.";
	}

	// 해당 조명 불러오는 메서드
	public LightDetailResponseDto getLightDetail(Long userId, Long lightId) {
		Light light = findLightById(lightId);

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(light.getUser(), userId);

		return LightDetailResponseDto.builder()
			.lightId(light.getId())
			.nickname(light.getNickname())
			.wakeupColor(light.getWakeupColor())
			.curColor(light.getCurColor())
			.macAddress(light.getMacAddress())
			.isAccessible(light.isAccessible())
			.build();
	}

	// 조명 설정 업데이트 하는 메서드
	public String updateLight(Long userId, LightUpdateRequestDto lightUpdateRequestDto) {
		Light light = findLightById(lightUpdateRequestDto.lightId());

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(light.getUser(), userId);

		light.setNickname(lightUpdateRequestDto.nickname());
		light.setWakeupColor(lightUpdateRequestDto.wakeupColor());
		light.setCurColor(lightUpdateRequestDto.curColor());
		light.setAccessible(lightUpdateRequestDto.isAccessible());
		lightRepository.save(light);

		return "조명 설정이 업데이트 되었습니다.";
	}

	// 조명 mac 주소로 조명을 찾는 메서드
	public Light findLightByMacAddress(String macAddress) {
		return lightRepository.findByMacAddress(macAddress)
			.orElseThrow(() -> new IllegalArgumentException("해당 MAC 주소를 가진 조명이 존재하지 않습니다."));
	}

	// 조명 id 로 조명을 찾는 메서드
	public Light findLightById(Long lightId) {
		return lightRepository.findById(lightId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 조명이 존재하지 않습니다."));
	}

	// 기기 주인인지 확인
	public void checkUserAuthority(User user, Long userId) {
		if (!user.getId().equals(userId)) {
			throw new IllegalArgumentException("권한이 없는 사용자입니다.");
		}
	}

	// 조명의 현재색 정보를 저장하는 메서드
	public void setLightCurColor(Long lightId, String curColor) {
		Light light = findLightById(lightId);
		light.setCurColor(curColor);
		lightRepository.save(light);
	}

	// 조명의 현재색 정보를 가져오는 메서드
	public String getLightCurColor(Long lightId) {
		Light light = findLightById(lightId);
		return light.getCurColor();
	}
}

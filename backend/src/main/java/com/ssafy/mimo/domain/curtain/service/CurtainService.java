package com.ssafy.mimo.domain.curtain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.domain.curtain.dto.CurtainDetailResponseDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterRequestDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainRegisterResponseDto;
import com.ssafy.mimo.domain.curtain.dto.CurtainUpdateRequestDto;
import com.ssafy.mimo.domain.curtain.entity.Curtain;
import com.ssafy.mimo.domain.curtain.repository.CurtainRepository;
import com.ssafy.mimo.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CurtainService {
	private final CurtainRepository curtainRepository;
	private final CurtainRegisterService curtainRegisterService;

	// 커튼 등록하는 메서드
	public CurtainRegisterResponseDto registerCurtain(Long userId, CurtainRegisterRequestDto curtainRegisterRequestDto) {
		String macAddress = curtainRegisterRequestDto.macAddress();

		// db 에 없는 신규 기기인 경우
		if (curtainRepository.findByMacAddress(macAddress).isEmpty()) {
			return curtainRegisterService.registerNewCurtain(userId, curtainRegisterRequestDto);
		}

		// 기존에 db에 있는 경우 해당 커튼 불러오기
		Curtain curtain = findCurtainByMacAddress(macAddress);

		// 등록해제 되어 있는 기기인 경우 다시 등록 절차
		if (!curtain.isRegistered()) {
			return curtainRegisterService.registerOldCurtain(userId, curtain, curtainRegisterRequestDto);
		}

		// 이미 등록된 기기인 경우 오류 안내
		throw new IllegalArgumentException("이미 등록된 커튼입니다.");
	}

	// 무드등 등록해제 하는 메서드
	public String unregisterCurtain(Long userId, Long curtainId) {
		Curtain curtain = findCurtainById(curtainId);

		// 이미 등록 해제된 경우
		if (!curtain.isRegistered()) {
			throw new IllegalArgumentException("이미 등록 해제된 커튼입니다.");
		}

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(curtain.getUser(), userId);

		// 커튼 객체 수정 및 저장
		curtain.setUser(null);
		curtain.setHub(null);
		curtain.setUnregisteredDttm(LocalDateTime.now());
		curtain.setRegistered(false);
		curtainRepository.save(curtain);

		return "커튼이 등록 해제되었습니다.";
	}

	// 해당 무드등 상세 불러오는 메서드
	public CurtainDetailResponseDto getCurtainDetail(Long userId, Long curtainId) {
		Curtain curtain = findCurtainById(curtainId);

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(curtain.getUser(), userId);

		return CurtainDetailResponseDto.builder()
			.curtainId(curtain.getId())
			.nickname(curtain.getNickname())
			.openDegree(curtain.getOpenDegree())
			.macAddress(curtain.getMacAddress())
			.isAccessible(curtain.isAccessible())
			.build();
	}

	// 커튼 설정 업데이트 하는 메서드
	public String updateCurtain(Long userId, CurtainUpdateRequestDto curtainUpdateRequestDto) {
		Curtain curtain = findCurtainById(curtainUpdateRequestDto.curtainId());

		// 해당 유저가 기기 주인인지 확인
		checkUserAuthority(curtain.getUser(), userId);

		// 커튼 객체 수정 및 저장
		curtain.setNickname(curtainUpdateRequestDto.nickname());
		curtain.setOpenDegree(curtainUpdateRequestDto.openDegree());
		curtain.setAccessible(curtainUpdateRequestDto.isAccessible());
		curtainRepository.save(curtain);

		return "커튼 설정이 업데이트 되었습니다.";
	}

	// 커튼 mac 주소로 커튼 찾기
	public Curtain findCurtainByMacAddress(String macAddress) {
		return curtainRepository.findByMacAddress(macAddress)
				.orElseThrow(() -> new IllegalArgumentException("해당 MAC 주소를 가진 커튼이 존재하지 않습니다."));
	}

	// 커튼 id로 커튼 찾기
	public Curtain findCurtainById(Long curtainId) {
		return curtainRepository.findById(curtainId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 커튼이 존재하지 않습니다."));
	}

	// 기기 주인인지 확인
	private void checkUserAuthority(User user, Long userId) {
		if (!user.getId().equals(userId)) {
			throw new IllegalArgumentException("권한이 없는 사용자입니다.");
		}
	}

}

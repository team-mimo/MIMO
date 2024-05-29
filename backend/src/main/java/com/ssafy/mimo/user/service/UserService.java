package com.ssafy.mimo.user.service;

import static com.ssafy.mimo.common.DeviceDefaults.*;

import java.time.LocalTime;

import com.ssafy.mimo.user.dto.MyInfoResponseDto;
import com.ssafy.mimo.user.dto.WakeupTimeDto;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public User findUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
	}

	// 토큰을 통해 유저 아이디를 찾는 메소드
	public Long getUserId(String token) {
		return Long.parseLong(jwtTokenProvider.getUserPk(token));
	}

	// 유저의 집/허브 유무를 확인하는 메소드
	public MyInfoResponseDto getHomeAndHubInfo(Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		boolean hasHome = false;
		boolean hasHub = false;

		if (user != null && !user.getUserHouse().isEmpty()) {
			hasHome = true;
			hasHub = user.getUserHouse().stream()
				.anyMatch(userHouse -> !userHouse.getHouse().getHub().isEmpty());
		}

		return MyInfoResponseDto.builder()
			.userId(userId)
			.hasHome(hasHome)
			.hasHub(hasHub)
			.build();
	}

	// 유저의 기상시간을 가져오는 메소드
	public WakeupTimeDto getWakeupTime(Long userId) {
		User user = findUserById(userId);
		LocalTime wakeupTime = user.getWakeupTime();
		return WakeupTimeDto.builder()
			.wakeupTime(wakeupTime)
			.build();
	}

	// 유저의 기상시간 설정하는 메서드
	public WakeupTimeDto setWakeupTime(Long userId, WakeupTimeDto wakeupTimeDto) {
		User user = findUserById(userId);
		user.setWakeupTime(wakeupTimeDto.wakeupTime());
		userRepository.save(user);
		return WakeupTimeDto.builder()
			.wakeupTime(user.getWakeupTime())
			.build();
	}

	// 유저의 기상시간을 해제하는 메서드
	public String deleteWakeupTime(Long userId) {
		User user = findUserById(userId);
		user.setWakeupTime(null);
		userRepository.save(user);
		return "기상시간이 해제되었습니다.";
	}
}

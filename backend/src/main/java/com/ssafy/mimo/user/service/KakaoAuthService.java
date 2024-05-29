package com.ssafy.mimo.user.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.mimo.user.dto.KakaoUserInfoResponseDto;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoAuthService {

	private final KakaoUserInfo kakaoUserInfo;
	private final UserRepository userRepository;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public Long isSignedUp(String token) {
		KakaoUserInfoResponseDto userInfo = kakaoUserInfo.getUserInfo(token);
		String keyCode = userInfo.getId().toString();
		Optional<User> optionalUser = userRepository.findByKeyCode(keyCode);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return user.getId();
		} else {
			User user = signUp(userInfo);
			return user.getId();
		}
	}

	public User signUp(KakaoUserInfoResponseDto kakaoUserInfoResponseDto) {
		User user = User.builder()
			.keyCode(kakaoUserInfoResponseDto.getId().toString())
			.email(kakaoUserInfoResponseDto.getKakao_account().getEmail())
			.nickname(kakaoUserInfoResponseDto.getProperties().getNickname())
			.build();

		return userRepository.save(user);
	}

}

package com.ssafy.mimo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoResponseDto {

	private Long id;
	private String connected_at;
	private Properties properties;
	private KakaoAccount kakao_account;

	@Getter
	@Setter
	public static class Properties {
		private String nickname;
	}

	@Getter
	@Setter
	public static class KakaoAccount {
		private Boolean profile_nickname_needs_agreement;
		private Profile profile;
		private Boolean has_email;
		private Boolean emails_needs_agreement;
		private Boolean is_email_valid;
		private Boolean is_email_verified;
		private String email;
	}

	public static class Profile {
		private String nickname;
		Boolean is_default_nickname;
	}
}

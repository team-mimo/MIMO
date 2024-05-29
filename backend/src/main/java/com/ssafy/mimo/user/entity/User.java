package com.ssafy.mimo.user.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ssafy.mimo.common.BaseDeletableEntity;
import com.ssafy.mimo.domain.house.entity.UserHouse;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseDeletableEntity implements UserDetails {

	@Column(length = 100, nullable = false, unique = true)
	private String keyCode; // 로그인 식별키

	@ElementCollection(fetch = FetchType.EAGER) //roles 컬렉션
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	@Override   //사용자의 권한 목록 리턴
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return keyCode;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Builder.Default
	@NotNull
	private Boolean isSuperUser = false;

	@NotNull
	private String email;

	@NotNull
	private String nickname;

	@Nullable
	private LocalTime wakeupTime;

	@OneToMany(mappedBy = "user")
	private List<UserHouse> userHouse;

}

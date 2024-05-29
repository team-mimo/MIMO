package com.ssafy.mimo.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.mimo.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByKeyCode(String keyCode);
}

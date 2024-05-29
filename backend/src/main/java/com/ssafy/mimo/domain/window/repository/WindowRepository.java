package com.ssafy.mimo.domain.window.repository;

import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WindowRepository extends JpaRepository<SlidingWindow, Long> {
	Optional<SlidingWindow> findByMacAddress(String macAddress);

	List<SlidingWindow> findByHubId(Long hubId);
}

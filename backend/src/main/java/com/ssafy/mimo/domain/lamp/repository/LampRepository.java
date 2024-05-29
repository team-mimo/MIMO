package com.ssafy.mimo.domain.lamp.repository;

import com.ssafy.mimo.domain.lamp.entity.Lamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LampRepository extends JpaRepository<Lamp, Long> {
	Optional<Lamp> findByMacAddress(String macAddress);

	List<Lamp> findByHubId(Long hubId);
}

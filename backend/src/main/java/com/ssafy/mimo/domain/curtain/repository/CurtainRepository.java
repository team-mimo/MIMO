package com.ssafy.mimo.domain.curtain.repository;

import com.ssafy.mimo.domain.curtain.entity.Curtain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurtainRepository extends JpaRepository<Curtain, Long> {
	Optional<Curtain> findByMacAddress(String macAddress);

    List<Curtain> findByHubId(Long hubId);
}

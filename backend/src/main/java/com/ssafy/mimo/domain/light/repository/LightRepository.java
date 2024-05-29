package com.ssafy.mimo.domain.light.repository;

import com.ssafy.mimo.domain.light.entity.Light;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LightRepository extends JpaRepository<Light, Long> {
    Optional<Light> findByMacAddress(String macAddress);

    List<Light> findByHubId(Long hubId);
}

package com.ssafy.mimo.sleep.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.mimo.sleep.entity.SleepData;

public interface SleepDataRepository extends JpaRepository<SleepData, Long> {
	SleepData findTopByUserIdOrderByCreatedDttmDesc(Long userId);
}

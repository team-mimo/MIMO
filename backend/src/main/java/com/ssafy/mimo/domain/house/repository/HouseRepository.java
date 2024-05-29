package com.ssafy.mimo.domain.house.repository;

import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.hub.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {

    House findByHub(Hub hub);
}

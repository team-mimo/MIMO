package com.ssafy.mimo.domain.hub.repository;

import com.ssafy.mimo.domain.hub.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HubRepository extends JpaRepository<Hub, Long> {
    Optional<Hub> findBySerialNumber(String serialNumber);

    List<Hub> findByHouseId(Long houseId);
}

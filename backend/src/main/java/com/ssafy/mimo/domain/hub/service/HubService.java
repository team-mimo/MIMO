package com.ssafy.mimo.domain.hub.service;

import com.ssafy.mimo.domain.curtain.entity.Curtain;
import com.ssafy.mimo.domain.curtain.repository.CurtainRepository;
import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.house.service.HouseService;
import com.ssafy.mimo.domain.hub.dto.DeviceListResponseDto;
import com.ssafy.mimo.domain.hub.dto.HubListResponseDto;
import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.repository.HubRepository;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.lamp.repository.LampRepository;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.light.repository.LightRepository;
import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import com.ssafy.mimo.domain.window.repository.WindowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class HubService {
    private final HubRepository hubRepository;
    private final HouseService houseService;
    private final LightRepository lightRepository;
    private final LampRepository lampRepository;
    private final CurtainRepository curtainRepository;
    private final WindowRepository windowRepository;
    public String releaseHub() {
        Hub hub = Hub.builder()
                .serialNumber(UUID.randomUUID().toString())
                .nickname("새로운 허브")
                .build();
        hub = hubRepository.save(hub);
        return hub.getSerialNumber();
    }
    public List<HubListResponseDto> getHubs(Long houseId) {
        List<HubListResponseDto> response = new ArrayList<>();
        List<Hub> hubs = hubRepository.findByHouseId(houseId);
        for (Hub hub : hubs) {
            HubListResponseDto hubListResponseDto = HubListResponseDto.builder()
                    .hubId(hub.getId())
                    .serialNumber(hub.getSerialNumber())
                    .registeredDttm(hub.getRegisteredDttm())
                    .devices(getDevices(hub))
                    .build();
            response.add(hubListResponseDto);
        }
        return response;
    }
    public String registerHub(String serialNumber, Long houseId) {
        Hub hub = findHubBySerialNumber(serialNumber);
        House house = houseService.findHouseById(houseId);
        if (!hub.isRegistered()){
            hub.setRegistered(true);
            hub.setRegisteredDttm(LocalDateTime.now());
            hub.setHouse(house);
            hubRepository.save(hub);
            return "허브 등록 성공";
        } else {
            throw new IllegalArgumentException("이미 등록된 허브입니다.");
        }
    }
    public String unregisterHub(Long hubId, Long houseId) {
        Hub hub = findHubById(hubId);
        if (hub.isRegistered() && hub.getHouse().getId().equals(houseId)){
            hub.setRegistered(false);
            hub.setHouse(null);
            hubRepository.save(hub);
            return "허브 등록 해제 성공";
        } else {
            throw new IllegalArgumentException("등록되지 않은 허브이거나 해당 집에 등록되지 않은 허브입니다.");
        }
    }
    public String updateHubNickname(Long hubId, String nickname) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 허브가 존재하지 않습니다."));
        hub.setNickname(nickname);
        hubRepository.save(hub);
        return "허브 닉네임 변경 성공";
    }
    public Boolean isValidHub(Hub hub) {
        return hub.isRegistered();
    }
    public Hub findHubBySerialNumber(String serialNumber) {
        return hubRepository.findBySerialNumber(serialNumber)
                .orElse(null);
    }
    public Hub findHubById(Long hubId) {
        return hubRepository.findById(hubId)
                .orElse(null);
    }
    private List<DeviceListResponseDto> getDevices(Hub hub) {
        List<DeviceListResponseDto> devices = new ArrayList<>();
        // Light
        List<Light> lights = lightRepository.findByHubId(hub.getId());
        for (Light light : lights) {
            DeviceListResponseDto deviceListResponseDto = DeviceListResponseDto.builder()
                    .deviceId(light.getId())
                    .deviceType("light")
                    .macAddress(light.getMacAddress())
                    .build();
            devices.add(deviceListResponseDto);
        }
        // Lamp
        List<Lamp> lamps = lampRepository.findByHubId(hub.getId());
        for (Lamp lamp : lamps) {
            DeviceListResponseDto deviceListResponseDto = DeviceListResponseDto.builder()
                    .deviceId(lamp.getId())
                    .deviceType("lamp")
                    .macAddress(lamp.getMacAddress())
                    .build();
            devices.add(deviceListResponseDto);
        }
        // Curtain
        List<Curtain> curtains = curtainRepository.findByHubId(hub.getId());
        for (Curtain curtain : curtains) {
            DeviceListResponseDto deviceListResponseDto = DeviceListResponseDto.builder()
                    .deviceId(curtain.getId())
                    .deviceType("curtain")
                    .macAddress(curtain.getMacAddress())
                    .build();
            devices.add(deviceListResponseDto);
        }
        // Window
        List<SlidingWindow> windows = windowRepository.findByHubId(hub.getId());
        for (SlidingWindow window : windows) {
            DeviceListResponseDto deviceListResponseDto = DeviceListResponseDto.builder()
                    .deviceId(window.getId())
                    .deviceType("window")
                    .macAddress(window.getMacAddress())
                    .build();
            devices.add(deviceListResponseDto);
        }
        return devices;
    }
}

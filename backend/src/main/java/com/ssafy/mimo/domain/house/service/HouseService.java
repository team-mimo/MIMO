package com.ssafy.mimo.domain.house.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.mimo.common.BaseDeviceEntity;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDataDto;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDto;
import com.ssafy.mimo.domain.curtain.repository.CurtainRepository;
import com.ssafy.mimo.domain.house.dto.*;
import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.house.entity.UserHouse;
import com.ssafy.mimo.domain.house.repository.HouseRepository;
import com.ssafy.mimo.domain.house.repository.UserHouseRepository;
import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.repository.HubRepository;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.lamp.repository.LampRepository;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.light.repository.LightRepository;
import com.ssafy.mimo.domain.window.repository.WindowRepository;
import com.ssafy.mimo.socket.global.SocketController;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseService {

	private final UserRepository userRepository;
	private final HouseRepository houseRepository;
	private final UserHouseRepository userHouseRepository;
	private final HubRepository hubRepository;
	private final LampRepository lampRepository;
	private final LightRepository lightRepository;
	private final WindowRepository windowRepository;
	private final CurtainRepository curtainRepository;

	public List<HouseResponseDto> getHouses(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("사용자 ID는 null 이 될 수 없습니다.");
		}

		List<UserHouse> userHouses = userHouseRepository.findAllByUserId(userId);
		Map<String, HouseResponseDto> houseMap = new LinkedHashMap<>();

		for (UserHouse userHouse : userHouses) {
			House house = userHouse.getHouse();
			HouseResponseDto houseResponseDto = HouseResponseDto.builder()
					.houseId(house.getId())
					.nickname(userHouse.getNickname())
					.address(house.getAddress())
					.isHome(userHouse.isHome())
					.devices(house.getDevices())
					.build();

			// 동일한 주소의 경우, 가장 최근 것으로 업데이트
			houseMap.put(house.getAddress(), houseResponseDto);
		}

		return new ArrayList<>(houseMap.values());
	}

	public NewHouseResponseDto registerNewHouse(Long userId, NewHouseRequestDto newHouseRequestDto) {
		House house = new House();
		house.setAddress(newHouseRequestDto.address());
		house = houseRepository.save(house);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 ID " + userId + "를 찾을 수 없습니다."));

		// 기존에 등록된 집 중 isHome이 true인 경우 false로 업데이트
		userHouseRepository.findByUserAndIsHome(user, true).forEach(myHome -> {
			myHome.setHome(false);
			userHouseRepository.save(myHome);
		});

		// 새 집 등록
		UserHouse userHouse = UserHouse.builder()
				.user(user)
				.house(house)
				.nickname(newHouseRequestDto.nickname())
				.isHome(true)
				.build();
		userHouseRepository.save(userHouse);

		return new NewHouseResponseDto(userHouse.getHouse().getId());
	}

	public OldHouseResponseDto registerOldHouse(Long userId, OldHouseRequestDto oldHouseRequestDto) {
		Optional<Hub> optionalHub = hubRepository.findBySerialNumber(oldHouseRequestDto.serialNumber());
		if (optionalHub.isEmpty()) {
			return new OldHouseResponseDto(null, null);
		}

		// 허브가 있으면 추출
		Hub hub = optionalHub.get();

		Optional<House> optionalHouse = Optional.ofNullable(houseRepository.findByHub(hub));
		if (optionalHouse.isEmpty()) {
			return new OldHouseResponseDto(null, null);
		}

		// 집이 있으면 추출
		House house = optionalHouse.get();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

		userHouseRepository.findByUserAndIsHome(user, true).forEach(myHome -> {
			myHome.setHome(false);
			userHouseRepository.save(myHome);
		});

		UserHouse userHouse = UserHouse.builder()
				.user(user)
				.house(house)
				.isHome(true)
				.nickname("나의 집")
				.build();
		userHouseRepository.save(userHouse);

		return new OldHouseResponseDto(house.getId(), house.getAddress());
	}

	public void unregisterHouse(Long userId, Long houseId) {
		List<UserHouse> userHouses = userHouseRepository.findByHouseId(houseId);

		if (userHouses.isEmpty()) {
			throw new IllegalArgumentException("해당 ID를 가진 집을 찾을 수 없습니다: " + houseId);
		}

		UserHouse userHouse = userHouses.stream()
				.filter(uh -> uh.getUser().getId().equals(userId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("집을 삭제할 권한이 없습니다."));

		userHouse.setActive(false);
		userHouseRepository.save(userHouse);

		// 해당 House에 더 이상 활성화된 UserHouse가 없는지 확인
		House house = userHouse.getHouse();
		boolean activeUserHousesExist = userHouseRepository.findByHouseAndIsActive(house, true).stream()
				.anyMatch(UserHouse::isActive);

		if (!activeUserHousesExist) {
			house.setActive(false);
			houseRepository.save(house);
		}
	}

	public void updateHouseNickname(Long userId, Long houseId, HouseNicknameRequestDto houseNicknameRequestDto) {
		List<UserHouse> userHouses = userHouseRepository.findByHouseId(houseId);
		if (userHouses.isEmpty()) {
			throw new IllegalArgumentException("해당 ID를 가진 집을 찾을 수 없습니다: " + houseId);
		}

		UserHouse userHouse = userHouses.stream()
				.filter(uh -> uh.getUser().getId().equals(userId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("집을 수정할 권한이 없습니다."));

		userHouse.updateNickname(houseNicknameRequestDto.nickname());
	}

	public boolean updateHouseStatus(Long userId, Long houseId) {
		// 현재 거주지인 집을 찾아 해제
		UserHouse currentHome = userHouseRepository.findHomeByUserIdAndIsHome(userId, true);
		if (!currentHome.getId().equals(houseId)) {
			currentHome.deactivateHome();
			userHouseRepository.save(currentHome);
		}

		// 새로운 집을 현재 거주지로 설정
		UserHouse newHome = userHouseRepository.findByUserIdAndHouseId(userId, houseId).orElse(null);
		if (newHome == null) return false; // 새 집이 없는 경우

		newHome.activateHome();
		userHouseRepository.save(newHome);

		return true;
	}

	public HouseDeviceResponseDto getDevices(Long userId, Long houseId) throws InterruptedException {
        UserHouse userHouse = userHouseRepository.findByUserIdAndHouseId(userId, houseId)
                .orElseThrow(() -> new RuntimeException("해당 사용자 ID와 집 ID를 가진 현재 거주지를 찾을 수 없습니다: userId=" + userId + ", houseId=" + houseId));

        House house = userHouse.getHouse();
        if (house == null) {
            throw new RuntimeException("UserHouse (" + userId + ", " + houseId + ")에 연결된 House가 없습니다.");
        }

        // House에 등록된 모든 Hub 찾기
        List<Hub> hubs = hubRepository.findByHouseId(house.getId());

        // 모든 디바이스 타입 별로 조회 및 DTO 변환
        List<DeviceDetailDto> allDevices = new ArrayList<>();
        Long hubId;
        for (Hub hub : hubs) {
            hubId = hub.getId();
            allDevices.addAll(getDevicesForHub(hubId, "lamp"));
            allDevices.addAll(getDevicesForHub(hubId, "light"));
			allDevices.addAll(getDevicesForHub(hubId, "curtain"));
            allDevices.addAll(getDevicesForHub(hubId, "window"));
        }

		return HouseDeviceResponseDto.builder()
				.houseId(house.getId())
				.nickname(userHouse.getNickname())
				.address(house.getAddress())
				.isHome(userHouse.isHome())
				.devices(allDevices)
				.build();
    }

	private List<DeviceDetailDto> getDevicesForHub(Long hubId, String type) throws InterruptedException {
		List<DeviceDetailDto> deviceDetails = new ArrayList<>();
		List<? extends BaseDeviceEntity> devices = findDevices(hubId, type);

		for (BaseDeviceEntity device : devices) {
			DeviceDetailDto deviceDetailDto = deviceDetail(device, hubId, type);
			if (deviceDetailDto == null) {
				String color = null;
				if ("lamp".equals(type)) {
					Lamp lamp = (Lamp) device;
					color = lamp.getCurColor();
				} else if ("light".equals(type)) {
					Light light = (Light) device;
					color = light.getCurColor();
				}

				deviceDetailDto = DeviceDetailDto.builder()
						.userId(device.getUser().getId())
						.hubId(hubId)
						.deviceId(device.getId())
						.nickname(device.getNickname())
						.isAccessible(device.isAccessible())
						.type(type)
						.color(color)
						.curColor(null)
						.openDegree(null)
						.build();
			}
			deviceDetails.add(deviceDetailDto);
		}

		return deviceDetails;
	}

	private List<? extends BaseDeviceEntity> findDevices(Long hubId, String type) {
        return switch (type) {
            case "lamp" -> lampRepository.findByHubId(hubId);
            case "light" -> lightRepository.findByHubId(hubId);
			case "curtain" -> curtainRepository.findByHubId(hubId);
            case "window" -> windowRepository.findByHubId(hubId);
            default -> throw new IllegalArgumentException("Invalid device type: " + type);
        };
	}

	private DeviceDetailDto deviceDetail(BaseDeviceEntity device, Long hubId, String type) throws InterruptedException {
		Integer curColor = null;
		Integer openDegree = null;

		ManualControlRequestDto manualControlRequestDto = ManualControlRequestDto.builder()
				.type(type)
				.deviceId(device.getId())
				.data(ManualControlRequestDataDto.builder()
						.requestName("getState")
						.build())
				.build();

		String response = SocketController.getMessage(hubId, SocketController.sendMessage(hubId, manualControlRequestDto.toString()));
		if (response == null) {
			return null;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode responseNode = (ObjectNode) objectMapper.readTree(response);
			String stateValue = responseNode.get("data").get("state").asText(null);
			int state = Integer.parseInt(stateValue);
			if ("lamp".equals(type) || "light".equals(type)) {
				curColor = state; // 현재 온/오프 상태
			} else if ("curtain".equals(type) || "window".equals(type)) {
				openDegree = state; // 현재 개방 상태
			}
		} catch (Exception e) {
			return null;
		}

		String color = null;
		if ("lamp".equals(type)) {
			Lamp lamp = (Lamp) device;
			color = lamp.getCurColor();
		} else if ("light".equals(type)) {
			Light light = (Light) device;
			color = light.getCurColor();
		}

		return DeviceDetailDto.builder()
				.userId(device.getUser().getId())
				.hubId(hubId)
				.deviceId(device.getId())
				.nickname(device.getNickname())
				.isAccessible(device.isAccessible())
				.type(type)
				.color(color)
				.curColor(curColor)
				.openDegree(openDegree)
				.build();
	}

	public HouseDeviceListResponseDto getDeviceList(Long userId, Long houseId) {
		UserHouse userHouse = userHouseRepository.findByUserIdAndHouseId(userId, houseId)
				.orElseThrow(() -> new RuntimeException("해당 사용자 ID와 집 ID를 가진 현재 거주지를 찾을 수 없습니다: userId=" + userId + ", houseId=" + houseId));

		House house = userHouse.getHouse();
		if (house == null) {
			throw new RuntimeException("UserHouse (" + userId + ", " + houseId + ")에 연결된 House가 없습니다.");
		}

		// House에 등록된 모든 Hub 찾기
		List<Hub> hubs = hubRepository.findByHouseId(house.getId());
		List<DeviceListDto> allDevices = new ArrayList<>();
		for (Hub hub : hubs) {
			Long hubId = hub.getId();
			allDevices.addAll(lampRepository.findByHubId(hubId).stream()
					.map(device -> DeviceListDto.builder()
							.userId(Objects.requireNonNull(device.getUser()).getId())
							.hubId(hubId)
							.deviceId(device.getId())
							.type("lamp")
							.nickname(device.getNickname())
							.isAccessible(device.isAccessible())
							.build())
					.toList());

			allDevices.addAll(lightRepository.findByHubId(hubId).stream()
					.map(device -> DeviceListDto.builder()
							.userId(Objects.requireNonNull(device.getUser()).getId())
							.hubId(hubId)
							.deviceId(device.getId())
							.type("light")
							.nickname(device.getNickname())
							.isAccessible(device.isAccessible())
							.build())
					.toList());

			allDevices.addAll(curtainRepository.findByHubId(hubId).stream()
					.map(device -> DeviceListDto.builder()
							.userId(Objects.requireNonNull(device.getUser()).getId())
							.hubId(hubId)
							.deviceId(device.getId())
							.type("curtain")
							.nickname(device.getNickname())
							.isAccessible(device.isAccessible())
							.build())
					.toList());

			allDevices.addAll(windowRepository.findByHubId(hubId).stream()
					.map(device -> DeviceListDto.builder()
							.userId(Objects.requireNonNull(device.getUser()).getId())
							.hubId(hubId)
							.deviceId(device.getId())
							.type("window")
							.nickname(device.getNickname())
							.isAccessible(device.isAccessible())
							.build())
					.toList());
		}

		return HouseDeviceListResponseDto.builder()
				.houseId(house.getId())
				.nickname(userHouse.getNickname())
				.address(house.getAddress())
				.isHome(userHouse.isHome())
				.devices(allDevices)
				.build();
	}

	public House findHouseById(Long houseId) {
		return houseRepository.findById(houseId)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 집이 존재하지 않습니다."));
	}
}

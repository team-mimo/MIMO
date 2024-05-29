package com.ssafy.mimo.sleep.service;

import com.ssafy.mimo.domain.common.dto.ManualControlRequestDataDto;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDto;
import com.ssafy.mimo.domain.common.service.CommonService;
import com.ssafy.mimo.domain.curtain.entity.Curtain;
import com.ssafy.mimo.domain.curtain.service.CurtainService;
import com.ssafy.mimo.domain.house.dto.DeviceDetailDto;
import com.ssafy.mimo.domain.house.dto.DeviceListDto;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.lamp.service.LampService;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.light.service.LightService;
import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import com.ssafy.mimo.domain.window.service.WindowService;
import com.ssafy.mimo.user.entity.User;
import com.ssafy.mimo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceHandlerService {
	private final UserService userService;
	private final LightService lightService;
	private final LampService lampService;
	private final WindowService windowService;
	private final CurtainService curtainService;
	private final CommonService commonService;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public void handleOnSleep(DeviceListDto device) {
		String type = device.type();
		switch (type) {
			case "light":
				// 조명 끄기
				ManualControlRequestDto lightManualControlRequestDto = ManualControlRequestDto.builder()
					.type("light")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setStateOff")
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lightManualControlRequestDto);
				log.info("조명 끄기 요청: {}", lightManualControlRequestDto);
				break;
			case "lamp":
				// 무드등 끄기
				ManualControlRequestDto lampManualControlRequestDto = ManualControlRequestDto.builder()
					.type("lamp")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setStateOff")
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lampManualControlRequestDto);
				log.info("무드등 끄기 요청: {}", lampManualControlRequestDto);
				break;
			case "window":
				// 창문 닫기
				ManualControlRequestDto windowManualControlRequestDto = ManualControlRequestDto.builder()
					.type("window")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setState")
						.state(0)
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(windowManualControlRequestDto);
				log.info("창문 닫기 요청: {}", windowManualControlRequestDto);
				break;
			case "curtain":
				// 커튼 닫기
				ManualControlRequestDto curtainManualControlRequestDto = ManualControlRequestDto.builder()
					.type("curtain")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setState")
						.state(0)
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(curtainManualControlRequestDto);
				log.info("커튼 닫기 요청: {}", curtainManualControlRequestDto);
				break;
			default:
				throw new IllegalArgumentException("지원하지 않는 기기 타입입니다: " + type);
		}
	}

	public void handleOnWakeUp(DeviceListDto device) {
		// IoT 기기 제어 로직
		String type = device.type();
		switch (type) {
			case "light":
				// 조명 켜기
				Light light = lightService.findLightById(device.deviceId());
				ManualControlRequestDto lightManualControlRequestDto = ManualControlRequestDto.builder()
					.type("light")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setCurrentColor")
						.color(light.getWakeupColor())
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lightManualControlRequestDto);
				log.info("조명 켜기 요청: {}", lightManualControlRequestDto);
				break;
			case "lamp":
				// 무드등 켜기
				Lamp lamp = lampService.findLampById(device.deviceId());
				ManualControlRequestDto lampManualControlRequestDto = ManualControlRequestDto.builder()
					.type("lamp")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setCurrentColor")
						.color(lamp.getWakeupColor())
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lampManualControlRequestDto);
				log.info("무드등 켜기 요청: {}", lampManualControlRequestDto);
				break;
			case "window":
				// 창문 열기
				SlidingWindow window = windowService.findWindowById(device.deviceId());
				ManualControlRequestDto windowManualControlRequestDto = ManualControlRequestDto.builder()
					.type("window")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setState")
						.state(window.getOpenDegree())
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(windowManualControlRequestDto);
				log.info("창문 열기 요청: {}", windowManualControlRequestDto);
				break;
			case "curtain":
				// 커튼 열기
				Curtain curtain = curtainService.findCurtainById(device.deviceId());
				ManualControlRequestDto curtainManualControlRequestDto = ManualControlRequestDto.builder()
					.type("curtain")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setState")
						.state(curtain.getOpenDegree())
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(curtainManualControlRequestDto);
				log.info("커튼 열기 요청: {}", curtainManualControlRequestDto);
				break;
			default:
				throw new IllegalArgumentException("지원하지 않는 기기 타입입니다: " + type);
		}
	}

	public void handleOnRem(Long userId, DeviceListDto device) {
		User user = userService.findUserById(userId);

		// 유저의 설정된 기상 시간이 없는 경우 return
		if (user.getWakeupTime() == null) {
			return;
		}

		// 현재 시간이 유저의 설정된 기상 시간 기준  1시간 전부터 설정 시간 사이가 아닌 경우 return
		LocalTime wakeupTimeBeforeOneHour = user.getWakeupTime().minusHours(1);
		if (LocalTime.now().isBefore(wakeupTimeBeforeOneHour) || LocalTime.now().isAfter(user.getWakeupTime())) {
			return;
		}

		// IoT 기기 제어 로직
		Integer leftMinutes = (int)(user.getWakeupTime().toSecondOfDay() - LocalTime.now().toSecondOfDay()) / 60;
		String type = device.type();
		switch (type) {
			case "light":
				// 조명 켜기
				Light light = lightService.findLightById(device.deviceId());
				ManualControlRequestDto lightManualControlRequestDto = ManualControlRequestDto.builder()
					.type("light")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setWakeupColor")
						.color(light.getWakeupColor())
						.time(leftMinutes)
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lightManualControlRequestDto);
				log.info("조명 서서히 켜기 요청: {}", lightManualControlRequestDto);
				break;
			case "lamp":
				// 무드등 켜기
				Lamp lamp = lampService.findLampById(device.deviceId());
				ManualControlRequestDto lampManualControlRequestDto = ManualControlRequestDto.builder()
					.type("lamp")
					.deviceId(device.deviceId())
					.data(ManualControlRequestDataDto.builder()
						.requestName("setWakeupColor")
						.color(lamp.getWakeupColor())
						.time(leftMinutes)
						.build())
					.build();
				// IoT 기기 제어요청 보내기
				commonService.controlDevice(lampManualControlRequestDto);
				log.info("무드등 서서히 켜기 요청: {}", lampManualControlRequestDto);
				break;
			case "window":
				break;
			case "curtain":
				break;
			default:
				throw new IllegalArgumentException("지원하지 않는 기기 타입입니다: " + type);
		}
	}

	// 무드등만 켜기!
	public void handleLampOn(DeviceDetailDto device) {
		String type = device.type();
		if (type == "lamp") {
			Lamp lamp = lampService.findLampById(device.deviceId());
			ManualControlRequestDto lampManualControlRequestDto = ManualControlRequestDto.builder()
				.type("lamp")
				.deviceId(device.deviceId())
				.data(ManualControlRequestDataDto.builder()
					.requestName("setCurrentColor")
					.color(lamp.getCurColor())
					.build())
				.build();
			// IoT 기기 제어요청 보내기
			commonService.controlDevice(lampManualControlRequestDto);
		}
	}
}

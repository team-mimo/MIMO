package com.ssafy.mimo.socket.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.mimo.domain.curtain.service.CurtainService;
import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.hub.service.HubService;
import com.ssafy.mimo.domain.lamp.service.LampService;
import com.ssafy.mimo.domain.light.service.LightService;
import com.ssafy.mimo.domain.window.service.WindowService;
import com.ssafy.mimo.socket.global.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class SocketService {
    private final HubService hubService;
    private final LightService lightService;
    private final LampService lampService;
    private final WindowService windowService;
    private final CurtainService curtainService;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    public Long getHubId(HubConnectionRequestDto hubConnectionRequestDto) {
        try {
            String serialNumber = hubConnectionRequestDto.getHubSerialNumber();
            // 시리얼 넘버로 등록된 허브 ID가 있는지 확인
            Hub hub = hubService.findHubBySerialNumber(serialNumber);
            if (hub != null && hubService.isValidHub(hub)) {  // 등록된 허브인지 확인
                return hub.getId();  // 등록된 시리얼 넘버에 대한 허브 ID 반환
            } else {
                log.warn("Invalid hub serial number: {}", serialNumber);
            }
        } catch (Exception e) {
            log.error("Error while getting the hub ID: {}", e.getMessage());
        }
        return null;  // 등록되지 않은 경우나 오류 발생 시 null 반환
    }
    public ObjectNode handleRequest(String request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(request);
            String type = jsonNode.get("type").asText();
            switch (type) {
                case "hub": // 허브 요청
                    // 기기 ID 요청
                    if (jsonNode.get("requestName").asText().equals("getId")) {
                        DeviceIdRequestDto deviceIdRequestDto = objectMapper.readValue(request, DeviceIdRequestDto.class);
                        Long deviceId;
                        String machineType = deviceIdRequestDto.machineType();
                        if (machineType == null) {
                            return null;
                        } else if (machineType.equals("light")) {
                            deviceId = lightService.findLightByMacAddress(deviceIdRequestDto.macAddress()).getId();
                        } else if (machineType.equals("lamp")) {
                            deviceId = lampService.findLampByMacAddress(deviceIdRequestDto.macAddress()).getId();
                        } else if (machineType.equals("window")) {
                            deviceId = windowService.findWindowByMacAddress(deviceIdRequestDto.macAddress()).getId();
                        } else if (machineType.equals("curtain")) {
                            deviceId = curtainService.findCurtainByMacAddress(deviceIdRequestDto.macAddress()).getId();
                        } else {
//                            return objectMapper.valueToTree("Invalid machine type: " + machineType);
                            return null;
                        }
                        DeviceIdResponseDto response = new DeviceIdResponseDto(deviceIdRequestDto, deviceId);
                        return objectMapper.valueToTree(response);
                    }
                case "light": // 조명 요청
                    LightControlRequestDto lightRequest = objectMapper.readValue(request, LightControlRequestDto.class);
                    if (lightRequest.getData().getRequestName().equals("getCurrentColor")) {
                        String curColor = lightService.getLightCurColor(lightRequest.getLightId());
                        LightControlResponseDto lightResponse = new LightControlResponseDto(lightRequest, lightRequest.getData(), curColor);
                        return objectMapper.valueToTree(lightResponse);
                    }
                {
//                    return objectMapper.valueToTree("Invalid request name: " + lightRequest.getData().getRequestName());
                    return null;
                }
                case "lamp": // 램프 요청
                    LampControlRequestDto lampRequest = objectMapper.readValue(request, LampControlRequestDto.class);
                    if (lampRequest.getData().getRequestName().equals("getCurrentColor")) {
                        String curColor = lampService.getLampCurColor(lampRequest.getLampId());
                        LampControlResponseDto lampResponse = new LampControlResponseDto(lampRequest, lampRequest.getData(), curColor);
                        return objectMapper.valueToTree(lampResponse);
                    }
//                    return objectMapper.valueToTree("Invalid request name: " + lampRequest.getData().getRequestName());
                    return null;
               case "curtain":
                   // CurtainControlRequestDto curtainRequest = objectMapper.readValue(request, CurtainControlRequestDto.class);
                   // if (curtainRequest.getData().getRequestName().equals("getCurrentStatus")) {
                   //     String curStatus = curtainService.getCurtainCurStatus(curtainRequest.getCurtainId());
                   //     CurtainControlResponseDto curtainResponse = new CurtainControlResponseDto(curtainRequest, curtainRequest.getData(), curStatus);
                   //     return objectMapper.valueToTree(curtainResponse);
                   // }
                   // return objectMapper.valueToTree("Invalid request name: " + curtainRequest.getData().getRequestName());
                   return null;
                case "window":
                    // WindowControlRequestDto windowRequest = objectMapper.readValue(request, WindowControlRequestDto.class);
                    // if (windowRequest.getData().getRequestName().equals("getCurrentStatus")) {
                    //     String curStatus = windowService.getWindowCurStatus(windowRequest.getWindowId());
                    //     WindowControlResponseDto windowResponse = new WindowControlResponseDto(windowRequest, windowRequest.getData(), curStatus);
                    //     return objectMapper.valueToTree(windowResponse);
                    // }
                    // return objectMapper.valueToTree("Invalid request name: " + windowRequest.getData().getRequestName());
                    return null;
                default:
                   // return objectMapper.valueToTree("Invalid request type: " + type);
                    return null;
            }
        } catch (Exception e) {
            log.error("Error while handling the request: {}", e.getMessage());
            return null;
        }
    }

    public static String readMessage(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        return new String(buffer, 0, bytesRead);
    }
}

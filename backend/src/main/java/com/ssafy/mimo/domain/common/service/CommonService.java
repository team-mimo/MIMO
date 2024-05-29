package com.ssafy.mimo.domain.common.service;

import com.ssafy.mimo.common.BaseDeviceEntity;
import com.ssafy.mimo.domain.common.dto.ManualControlRequestDto;
import com.ssafy.mimo.domain.curtain.service.CurtainService;
import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.domain.lamp.service.LampService;
import com.ssafy.mimo.domain.light.service.LightService;
import com.ssafy.mimo.domain.window.service.WindowService;
import com.ssafy.mimo.socket.global.MessageWriter;
import com.ssafy.mimo.socket.global.SocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final LightService lightService;
    private final LampService lampService;
    private final CurtainService curtainService;
    private final WindowService windowService;
    private final SocketController socketController;
    @Transactional
    public String controlDevice(ManualControlRequestDto manualControlRequestDto) {
        String type = manualControlRequestDto.getType();
        Long deviceId = manualControlRequestDto.getDeviceId();
        BaseDeviceEntity device;
        switch (type) {
            case "light":
                device = lightService.findLightById(deviceId);
                break;
            case "lamp":
                device = lampService.findLampById(deviceId);
                break;
            case "window":
                device = windowService.findWindowById(deviceId);
                break;
            case "curtain":
                device = curtainService.findCurtainById(deviceId);
                break;
            default:
                return "기기를 찾을 수 없습니다. 기기를 다시 확인해 주세요.";
        }
        Hub hub = device.getHub();
        if (hub == null)
            return "기기와 연결된 허브가 없습니다. 허브를 연결해 주세요.";
        try {
            String color = manualControlRequestDto.getData().getColor();
            String requestName = manualControlRequestDto.getData().getRequestName();
            // CRUD
            if (requestName.equals("setCurrentColor")) {
                if (type.equals("light")) {
                    lightService.setLightCurColor(deviceId, color);
                } else if (type.equals("lamp")) {
                    lampService.setLampCurColor(deviceId, color);
                }
            }
//            MessageWriter writer = SocketController.getMessageWriters().get(hub.getId());
//            writer.enqueueMessage(manualControlRequestDto.toString());
            String rid = socketController.sendMessage(hub.getId(), manualControlRequestDto.toString());
            if (rid != null) {
                List<String> rids = SocketController.getRequestIds().get(hub.getId());
                if (rids != null)
                    SocketController.getRequestIds().get(hub.getId()).remove(rid);
                SocketController.getReceivedMessages().remove(rid);
            }
        } catch (Exception e) {
            return "허브와 연결할 수 없습니다. 허브 연결을 확인해 주세요.";
        }
        return "명령 전송 완료!";
    }
}

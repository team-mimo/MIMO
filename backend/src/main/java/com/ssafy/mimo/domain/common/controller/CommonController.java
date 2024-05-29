package com.ssafy.mimo.domain.common.controller;

import com.ssafy.mimo.domain.common.dto.ManualControlRequestDto;
import com.ssafy.mimo.domain.common.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기기 수동제어 컨트롤러", description = "앱을 통한 기기의 수동제어 관련 기능이 포함되어 있음")
@RestController
@RequestMapping("/api/v1/control")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;
    @Operation(summary = "기기 수동제어")
    @PostMapping
    public ResponseEntity<String> controlDevice(@RequestBody ManualControlRequestDto manualControlRequestDto) {
        return ResponseEntity.ok(commonService.controlDevice(manualControlRequestDto));
    }
}

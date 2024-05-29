package com.ssafy.mimo.healthchecker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "헬스 체커", description = "서버 상태 확인을 위한 기능 없는 더미 API")
@RestController
@RequestMapping("/api/check")
public class HealthCheckerController {
    @Operation(summary = "서버 상태 확인")
    @GetMapping
    public String check() {
        return "I'm alive!";
    }
}

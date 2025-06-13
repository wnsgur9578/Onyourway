package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.domain.AutoMessageToggle;
import com.ssafy.onyourway.service.AutoMessageToggleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/toggle")
@RequiredArgsConstructor
public class AutoMessageToggleController {

    private final AutoMessageToggleService toggleService;

    @PostMapping("/{scheduleId}")
    public ResponseEntity<?> setToggle(
            @PathVariable Long scheduleId,
            @RequestBody Map<String, Object> body,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        boolean enabled = (boolean) body.get("enabled");
        String time = (String) body.get("time");
        toggleService.setToggle(userId, scheduleId, enabled, time);
        return ResponseEntity.ok("설정 완료");
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getToggle(
            @PathVariable Long scheduleId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        AutoMessageToggle toggle = toggleService.getToggle(userId, scheduleId);
        if (toggle == null) return ResponseEntity.ok().build();
        return ResponseEntity.ok(Map.of(
                "enabled", toggle.isEnabled(),
                "time", toggle.getTime()
        ));
    }
}

package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.service.KakaoMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://3.27.204.125:8080"})
@RestController
@RequestMapping("/api/kakao")
@RequiredArgsConstructor
public class KakaoMessageController {

    private final KakaoMessageService kakaoMessageService;

    @PostMapping("/send-to-me")
    public ResponseEntity<String> sendMessageToMe(@RequestBody Map<String, String> body) {
        String kakaoAccessToken = body.get("kakaoAccessToken");
        String scheduleId = body.get("scheduleId");

        if (kakaoAccessToken == null || scheduleId == null) {
            return ResponseEntity.badRequest().body("kakaoAccessToken 또는 scheduleId가 누락되었습니다.");
        }

        kakaoMessageService.sendMessageToMe(kakaoAccessToken, scheduleId);
        return ResponseEntity.ok("메시지 전송 성공");
    }

}

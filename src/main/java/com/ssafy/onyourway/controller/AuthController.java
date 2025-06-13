package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.dto.LoginRequest;
import com.ssafy.onyourway.dto.LoginResponse;
import com.ssafy.onyourway.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://3.27.204.125:8080"})
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    @Operation(summary = "카카오 로그인 처리")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginWithKakao(request.getAccessToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao/callback")
    @Operation(summary = "카카오 OAuth2 code로 로그인")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        LoginResponse response = authService.loginWithKakaoCode(code);
        return ResponseEntity.ok(response);
    }
}

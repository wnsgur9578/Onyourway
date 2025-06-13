package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.dto.UserDto;
import com.ssafy.onyourway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<UserDto.Response> getMyInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName()); // JwtUtil에서 subject로 userId 넣었음
        UserDto.Response dto = userService.getUserInfo(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/unlink")
    @Operation(summary = "카카오 연결 해제")
    public ResponseEntity<Void> unlinkKakao(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName()); // subject = userId
        userService.disconnectKakaoAccount(userId);
        return ResponseEntity.noContent().build(); // 204
    }

}

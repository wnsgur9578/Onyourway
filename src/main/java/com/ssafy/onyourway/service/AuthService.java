package com.ssafy.onyourway.service;

import com.ssafy.onyourway.dto.LoginResponse;

public interface AuthService {
    LoginResponse loginWithKakao(String accessToken);

    LoginResponse loginWithKakaoCode(String code);
}

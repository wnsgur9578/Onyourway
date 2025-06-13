package com.ssafy.onyourway.service;

public interface KakaoMessageService {
    void sendMessageToMe(String kakaoAccessToken, String message);
}

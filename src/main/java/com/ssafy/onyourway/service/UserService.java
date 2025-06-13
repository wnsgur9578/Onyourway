package com.ssafy.onyourway.service;

import com.ssafy.onyourway.dto.UserDto;

public interface UserService {
    UserDto.Response getUserInfo(Long userId);

    void disconnectKakaoAccount(Long userId);
}

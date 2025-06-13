package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.AutoMessageToggle;

import java.util.List;

public interface    AutoMessageToggleService {
    void setToggle(Long userId, Long scheduleId, boolean enabled, String time);
    AutoMessageToggle getToggle(Long userId, Long scheduleId);
    List<AutoMessageToggle> findAllEnabled(); // ✅ 추가됨
    void disableToggle(Long toggleId);

}

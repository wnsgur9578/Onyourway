package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.AutoMessageToggle;
import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.repository.AutoMessageToggleRepository;
import com.ssafy.onyourway.repository.ScheduleRepository;
import com.ssafy.onyourway.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoMessageToggleServiceImpl implements AutoMessageToggleService {

    private final AutoMessageToggleRepository toggleRepo;
    private final UserRepository userRepo;
    private final ScheduleRepository scheduleRepo;

    @Transactional
    @Override
    public void setToggle(Long userId, Long scheduleId, boolean enabled, String time) {
        AutoMessageToggle toggle = toggleRepo.findByUserIdAndScheduleId(userId, scheduleId)
                .orElseGet(() -> AutoMessageToggle.builder()
                        .user(userRepo.findById(userId).orElseThrow())
                        .schedule(scheduleRepo.findById(scheduleId).orElseThrow())
                        .build());

        toggle.setEnabled(enabled);
        toggle.setTime(time);

        if (enabled) {
            if (time != null && !time.isEmpty()) {
                toggle.setAlarmTime(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                throw new IllegalArgumentException("알람 시간은 필수입니다.");
            }
        } else {
            // OFF일 경우 알람 시간 제거
            toggle.setAlarmTime(null);
        }

        // 일정 종료 여부는 ON일 때만 체크
        if (enabled) {
            Schedule schedule = toggle.getSchedule();
            if (schedule.getEndDate() != null && schedule.getEndDate().isBefore(java.time.LocalDate.now())) {
                throw new IllegalStateException("종료된 일정에는 알람을 설정할 수 없습니다.");
            }
        }

        toggleRepo.save(toggle);
    }


    @Override
    public AutoMessageToggle getToggle(Long userId, Long scheduleId) {
        return toggleRepo.findByUserIdAndScheduleId(userId, scheduleId).orElse(null);
    }

    @Override
    public List<AutoMessageToggle> findAllEnabled() {
        return toggleRepo.findAllByEnabledTrue();
    }
    @Transactional
    @Override
    public void disableToggle(Long toggleId) {
        AutoMessageToggle toggle = toggleRepo.findById(toggleId).orElseThrow();
        toggle.setEnabled(false);
        toggleRepo.save(toggle); // ✅ 명시적 저장
    }
}

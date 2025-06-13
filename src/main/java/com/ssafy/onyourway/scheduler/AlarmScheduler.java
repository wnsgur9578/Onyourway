package com.ssafy.onyourway.scheduler;

import com.ssafy.onyourway.domain.AutoMessageToggle;
import com.ssafy.onyourway.service.AutoMessageToggleService;
import com.ssafy.onyourway.service.KakaoMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final AutoMessageToggleService toggleService;
    private final KakaoMessageService kakaoMessageService;

    @Scheduled(cron = "0 * * * * *")
    public void sendAutoMessages() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul")).withSecond(0).withNano(0);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<AutoMessageToggle> toggles = toggleService.findAllEnabled();

        for (AutoMessageToggle toggle : toggles) {
            LocalTime alarmTime = toggle.getAlarmTime();
            LocalDate endDate = toggle.getSchedule().getEndDate();

            if (alarmTime.equals(now) && !today.isAfter(endDate)) {
                kakaoMessageService.sendMessageToMe(
                        toggle.getUser().getKakaoAccessToken(),
                        toggle.getSchedule().getId().toString()
                );
                System.out.println("⏰ 자동 메시지 발송됨: " + toggle.getSchedule().getTitle());

                if (today.equals(endDate)) {
                    toggleService.disableToggle(toggle.getId());
                    System.out.println("📴 마지막 날 → 토글 비활성화됨 (ID: " + toggle.getId() + ")");
                }
            }
        }
    }


}

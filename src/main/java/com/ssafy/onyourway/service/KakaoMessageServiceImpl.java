package com.ssafy.onyourway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.onyourway.domain.ScheduleItem;
import com.ssafy.onyourway.repository.ScheduleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoMessageServiceImpl implements KakaoMessageService {

    private final ScheduleItemRepository scheduleItemRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

    @Override
    public void sendMessageToMe(String kakaoAccessToken, String scheduleIdStr) {
        Long scheduleId = Long.parseLong(scheduleIdStr);
        LocalDate today = LocalDate.now();
        List<ScheduleItem> items = scheduleItemRepository.findByScheduleIdAndDay(scheduleId, today);

        StringBuilder sb = new StringBuilder();
        sb.append("✈\uFE0F오늘의 일정✈\uFE0F\n")
                .append("\n");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (items == null || items.isEmpty()) {
            sb.append("오늘은 등록된 일정이 없습니다.");
        } else {
            for (ScheduleItem item : items) {
                String time = item.getTime() != null ? item.getTime().format(timeFormatter) : "시간 미정";
                String title = item.getTitle() != null ? item.getTitle() : "제목 없음";
                String location = item.getLocationName() != null ? item.getLocationName() : "위치 미정";

                sb.append("- ")
                        .append(time)
                        .append(" ")
                        .append(title)
                        .append("\n")
                        .append(location)
                        .append("\n")
                        .append("\n");
            }
        }
        System.out.println(">>> accessToken: " + kakaoAccessToken);
        String message = sb.toString();
        System.out.println("📨 최종 메시지:\n" + message);

        try {
            Map<String, Object> templateObject = new HashMap<>();
            templateObject.put("object_type", "text");
            templateObject.put("text", message);
            templateObject.put("link", Map.of(
                    "web_url", "http://3.27.204.125",
                    "mobile_web_url", "http://3.27.204.125"
            ));
            templateObject.put("button_title", "일정 확인하기");

            String templateJson = objectMapper.writeValueAsString(templateObject);
            System.out.println("템플릿 JSON:\n" + templateJson);

            // 카카오 메시지 전송
            String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + kakaoAccessToken);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("template_object", templateJson);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("카카오 메시지 전송 실패: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 메시지 전송 중 예외 발생", e);
        }
    }
}

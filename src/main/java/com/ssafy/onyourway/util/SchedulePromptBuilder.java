package com.ssafy.onyourway.util;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.domain.ScheduleItem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SchedulePromptBuilder {

    public String build(Schedule schedule) {
        List<ScheduleItem> items = schedule.getItems();

        StringBuilder sb = new StringBuilder();
        sb.append("너는 여행 일정을 요약하고 분석해주는 AI야. 내가 줄 정보를 기반으로 다음 두 가지를 작성해줘:\n");
        sb.append("1. 요약: 전체 일정을 짧고 자연스러운 평문으로 요약해줘.\n");
        sb.append("2. 한줄평 및 추천: 이 일정의 한줄평을 간단히 분석하고, 여행자의 입장에서 추천 팁이나 코스를 말해줘.\n");
        sb.append("두 항목은 아래 포맷을 꼭 지켜서 반환해줘:\n");
        sb.append("요약: [여기에 요약 내용]\n");
        sb.append("한줄평 및 추천: [여기에 추천 내용]\n\n");

        sb.append("다음은 여행 일정이야:\n");

        Map<LocalDate, List<ScheduleItem>> grouped = items.stream()
                .collect(Collectors.groupingBy(ScheduleItem::getDay));

        for (LocalDate day : grouped.keySet().stream().sorted().toList()) {
            sb.append("- 날짜: ").append(day).append("\n");
            grouped.get(day).stream()
                    .sorted(Comparator.comparing(ScheduleItem::getTime))
                    .forEach(item -> sb.append("  - ")
                            .append(item.getTime()).append(", ")
                            .append(item.getLocationName()).append(", ")
                            .append(item.getTitle())
                            .append(item.getMemo() != null ? ", 메모: " + item.getMemo() : "")
                            .append("\n"));
        }

        sb.append("\n위 내용을 기반으로 두 항목을 만들어줘. 질문은 하지 말고 바로 작성해줘. 반드시 높임말로 답변해줘.");
        return sb.toString();
    }
}

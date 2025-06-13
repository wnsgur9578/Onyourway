package com.ssafy.onyourway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GPT가 생성한 일정 요약 응답 DTO")
public class ScheduleSummaryResponse {

    @Schema(description = "GPT가 생성한 여행 요약 문장", example = "2025년 5월 13일, 마포대교와 동십자각을 방문하며 하루를 시작했습니다.")
    private String summary;
}
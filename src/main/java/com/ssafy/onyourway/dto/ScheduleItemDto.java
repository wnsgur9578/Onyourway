package com.ssafy.onyourway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleItemDto {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "일정 항목 생성 요청 DTO")
    public static class CreateRequest {
        @NotNull
        private LocalDate day;
        private LocalTime time;

        @NotBlank
        private String title;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private String memo;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "일정 항목 응답 DTO")
    public static class Response {
        private Long id;
        private LocalDate day;
        private LocalTime time;
        private String title;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private String memo;
    }
}


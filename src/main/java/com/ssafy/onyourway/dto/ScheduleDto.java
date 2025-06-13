package com.ssafy.onyourway.dto;

import com.ssafy.onyourway.domain.ScheduleItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class ScheduleDto {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "일정 생성 요청 DTO")
    public static class CreateRequest {
        @NotBlank
        private String title;

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "일정 수정 요청 DTO")
    public static class UpdateRequest {
        @NotBlank
        private String title;

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "일정 응답 DTO")
    public static class Response {
        private Long id;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<ScheduleItemDto.Response> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "일정 전체 상세 응답 DTO")
    public static class FullResponse {
        private Long id;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<ItemResponse> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "일정 항목 응답 DTO (일정 전체 조회용)")
    public static class ItemResponse {
        private String title;
        private String locationName;
        private double latitude;
        private double longitude;
        private String time;
        private String memo;
        private String day;

        public static ItemResponse from(ScheduleItem item) {
            return ItemResponse.builder()
                    .title(item.getTitle())
                    .locationName(item.getLocationName())
                    .latitude(item.getLatitude())
                    .longitude(item.getLongitude())
                    .time(String.valueOf(item.getTime()))
                    .memo(item.getMemo())
                    .day(String.valueOf(item.getDay()))
                    .build();
        }
    }
}

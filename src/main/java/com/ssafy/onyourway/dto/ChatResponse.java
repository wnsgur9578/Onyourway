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
@Schema(description = "GPT 응답 DTO")
public class ChatResponse {

    @Schema(description = "GPT가 응답한 메시지", example = "경복궁, 북촌한옥마을 등을 추천드립니다.")
    private String reply;
}

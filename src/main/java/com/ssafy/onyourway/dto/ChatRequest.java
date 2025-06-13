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
@Schema(description = "사용자 요청 DTO")
public class ChatRequest {

    @Schema(description = "GPT에게 보낼 메시지", example = "서울 여행지 추천해줘")
    private String message;
}

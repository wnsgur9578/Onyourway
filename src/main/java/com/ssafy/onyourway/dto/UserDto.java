package com.ssafy.onyourway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class UserDto {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    @Schema(description = "사용자 응답 DTO")
    public static class Response {
        private Long id;
        private String kakaoId;
        private String nickname;
        private String profileImage;
        private String email;
    }
}

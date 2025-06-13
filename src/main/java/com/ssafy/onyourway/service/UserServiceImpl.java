package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.User;
import com.ssafy.onyourway.dto.UserDto;
import com.ssafy.onyourway.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Override
    public UserDto.Response getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserDto.Response.builder()
                .nickname(user.getNickname())
                .kakaoId(user.getKakaoId())
                .profileImage(user.getProfileImage())
                .email(user.getEmail())
                .build();
    }

    @Override
    public void disconnectKakaoAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        user.getSchedules().clear();

        String accessToken = user.getKakaoAccessToken();
        log.info("🔗 사용자 unlink 요청: accessToken = {}", accessToken);

        try {
            WebClient.create()
                    .post()
                    .uri("https://kapi.kakao.com/v1/user/unlink")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(body -> log.info("✅ unlink 응답: {}", body))
                    .block();

            userRepository.delete(user);

        } catch (WebClientResponseException e) {
            log.error("❌ unlink 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 연결 해제 실패", e);
        }
    }

}

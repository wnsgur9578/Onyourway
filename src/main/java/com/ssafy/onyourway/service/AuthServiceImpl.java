package com.ssafy.onyourway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.onyourway.domain.User;
import com.ssafy.onyourway.dto.LoginResponse;
import com.ssafy.onyourway.repository.UserRepository;
import com.ssafy.onyourway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Override
    public LoginResponse loginWithKakao(String accessToken) {
        System.out.println("✅ Inject 확인: kakaoClientId = " + kakaoClientId);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                kakaoUserInfoUri,
                HttpMethod.GET,
                request,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response.getBody());
            String kakaoId = root.path("id").asText();
            String nickname = root.path("properties").path("nickname").asText();
            String profileImage = root.path("properties").path("profile_image").asText();
            String email = root.path("kakao_account").path("email").asText();

            System.out.println("KAKAO EMAIL : " + email);

            Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);
            User user = optionalUser.orElseGet(() -> User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .email(email)
                    .build());

            // ✅ access token 저장 또는 갱신
            user.setKakaoAccessToken(accessToken);
            userRepository.save(user);

            String token = jwtUtil.generateToken(user.getId().toString(), user.getNickname());
            return new LoginResponse(token, user.getNickname(), accessToken);

        } catch (Exception e) {
            throw new RuntimeException("카카오 로그인 실패", e);
        }
    }

    @Override
    public LoginResponse loginWithKakaoCode(String code) {
        System.out.println("✅ Inject 확인: kakaoClientId = " + kakaoClientId);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

            ResponseEntity<String> tokenResponse = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    tokenRequest,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            String accessToken = mapper.readTree(tokenResponse.getBody()).path("access_token").asText();

            // 사용자 정보 요청 및 저장 처리
            return loginWithKakao(accessToken);

        } catch (Exception e) {
            throw new RuntimeException("카카오 code 로그인 실패", e);
        }
    }
}
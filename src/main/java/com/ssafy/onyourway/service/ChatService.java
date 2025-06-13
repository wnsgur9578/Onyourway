package com.ssafy.onyourway.service;

import com.ssafy.onyourway.memory.ChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final ChatMemory chatMemory;

    public String askToGPT(String sessionId, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", "넌 사용자를 위해 응답하는 챗봇이야. " +
                        "반말은 절대 하지마. 높임말로만 답변해." + " 대화 주제는 대한민국 여행 일정 작성이야."
        ));

        messages.addAll(chatMemory.getHistory(sessionId));

        messages.add(Map.of("role", "user", "content", userMessage));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", messages);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", entity, Map.class
        );

        Map<String, Object> message = (Map<String, Object>)
                ((Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0)).get("message");

        String reply = (String) message.get("content");

        chatMemory.appendMessage(sessionId, "user", userMessage);
        chatMemory.appendMessage(sessionId, "assistant", reply);

        return reply;
    }
}

package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.dto.ChatRequest;
import com.ssafy.onyourway.dto.ChatResponse;
import com.ssafy.onyourway.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "GPT 챗봇 API", description = "사용자의 입력 메시지를 OpenAI GPT에게 전달하고 응답을 반환하는 API")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "GPT에게 대화 맥락을 포함하여 메시지 보내기")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request, HttpSession session) {
        String sessionId = session.getId(); // 세션 ID로 구분
        String reply = chatService.askToGPT(sessionId, request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }


}

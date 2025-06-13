package com.ssafy.onyourway.memory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatMemory {

    private final Map<String, List<Map<String, String>>> chatSessions = new ConcurrentHashMap<>();

    public List<Map<String, String>> getHistory(String sessionId) {
        return chatSessions.getOrDefault(sessionId, new ArrayList<>());
    }

    public void appendMessage(String sessionId, String role, String content) {
        chatSessions.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(Map.of("role", role, "content", content));
    }
}

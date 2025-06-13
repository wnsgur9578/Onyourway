package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.dto.ScheduleSummaryResponse;
import com.ssafy.onyourway.repository.ScheduleRepository;
import com.ssafy.onyourway.service.ChatService;
import com.ssafy.onyourway.util.SchedulePromptBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "일정 요약 API", description = "GPT를 활용하여 일정을 요약하는 기능")
public class ScheduleSummaryController {

    private final ScheduleRepository scheduleRepository;
    private final ChatService chatService;
    private final SchedulePromptBuilder promptBuilder;

    @GetMapping("/{id}/summary")
    @Operation(summary = "GPT 일정 요약",
            description = "일정 ID를 기반으로 해당 일정을 요약하여 자연스러운 문장으로 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요약 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ScheduleSummaryResponse.class))),
                    @ApiResponse(responseCode = "404", description = "일정 없음",
                            content = @Content(mediaType = "text/plain"))
            })
    public ResponseEntity<ScheduleSummaryResponse> summarize(@PathVariable Long id, HttpSession session) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정이 존재하지 않습니다."));

        String prompt = promptBuilder.build(schedule);
        String reply = chatService.askToGPT(session.getId(), prompt);

        return ResponseEntity.ok(new ScheduleSummaryResponse(reply));
    }
}

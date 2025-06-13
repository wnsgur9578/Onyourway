package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.dto.ScheduleDto;
import com.ssafy.onyourway.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "일정 생성")
    public ResponseEntity<Long> createSchedule(
            @RequestBody ScheduleDto.CreateRequest request,
            HttpServletRequest httpRequest
    ) {
        Long scheduleId = scheduleService.createSchedule(request, httpRequest);
        return ResponseEntity.ok(scheduleId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "일정 수정")
    public ResponseEntity<Void> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto.UpdateRequest request) {
        scheduleService.updateSchedule(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    @Operation(summary = "내 일정 목록 조회 (페이지네이션)")
    public ResponseEntity<List<ScheduleDto.Response>> getMySchedules(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Long userId = Long.parseLong(authentication.getName());
        List<ScheduleDto.Response> schedules = scheduleService.getMySchedulesPaged(userId, page, size);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    @Operation(summary = "일정 상세 조회")
    public ResponseEntity<ScheduleDto.Response> getSchedule(@PathVariable Long id) {
        ScheduleDto.Response response = scheduleService.getSchedule(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "일정 삭제")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/full")
    @Operation(summary = "일정 상세 + 항목 전체 조회")
    public ResponseEntity<ScheduleDto.FullResponse> getFullSchedule(@PathVariable Long id) {
        ScheduleDto.FullResponse fullResponse = scheduleService.getFullSchedule(id);
        return ResponseEntity.ok(fullResponse);
    }

    @PostMapping("/{id}/alarm")
    @Operation(summary = "일정 자동 알림 시간 설정")
    public ResponseEntity<Void> setAlarm(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String time = body.get("time");
        scheduleService.setAlarmTime(id, time);
        return ResponseEntity.ok().build();
    }
}
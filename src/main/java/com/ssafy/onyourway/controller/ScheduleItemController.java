package com.ssafy.onyourway.controller;

import com.ssafy.onyourway.dto.ScheduleItemDto;
import com.ssafy.onyourway.service.ScheduleItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ScheduleItemController.java
@RestController
@RequestMapping("/api/schedules/{scheduleId}/items")
@RequiredArgsConstructor
public class ScheduleItemController {

    private final ScheduleItemService scheduleItemService;

    @PostMapping
    @Operation(summary = "일정 항목 추가")
    public ResponseEntity<Long> addItem(@PathVariable Long scheduleId, @RequestBody ScheduleItemDto.CreateRequest request) {
        Long itemId = scheduleItemService.addScheduleItem(scheduleId, request);
        return ResponseEntity.ok(itemId);
    }

    @PostMapping("/bulk")
    @Operation(summary = "일정 항목 일괄 등록")
    public ResponseEntity<Void> createItemsBulk(
            @PathVariable Long scheduleId,
            @RequestBody List<ScheduleItemDto.CreateRequest> requests
    ) {
        scheduleItemService.createBulk(scheduleId, requests);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/bulk")
    @Operation(summary = "일정 항목 일괄 수정")
    public ResponseEntity<Void> updateItemsBulk(
            @PathVariable Long scheduleId,
            @RequestBody List<ScheduleItemDto.CreateRequest> requests
    ) {
        scheduleItemService.updateBulk(scheduleId, requests);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "일정 항목 수정")
    public ResponseEntity<Void> updateItem(@PathVariable Long scheduleId, @PathVariable Long itemId, @RequestBody ScheduleItemDto.CreateRequest request) {
        scheduleItemService.updateScheduleItem(scheduleId, itemId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "일정 항목 전체 조회")
    public ResponseEntity<List<ScheduleItemDto.Response>> getItems(@PathVariable Long scheduleId) {
        List<ScheduleItemDto.Response> items = scheduleItemService.getScheduleItems(scheduleId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "일정 항목 삭제")
    public ResponseEntity<Void> deleteItem(@PathVariable Long scheduleId, @PathVariable Long itemId) {
        scheduleItemService.deleteScheduleItem(scheduleId, itemId);
        return ResponseEntity.noContent().build();
    }
}

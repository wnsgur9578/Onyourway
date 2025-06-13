package com.ssafy.onyourway.service;

import com.ssafy.onyourway.dto.ScheduleItemDto;

import java.util.List;

// ScheduleItemService.java
public interface ScheduleItemService {
    Long addScheduleItem(Long scheduleId, ScheduleItemDto.CreateRequest request);
    void updateScheduleItem(Long scheduleId, Long itemId, ScheduleItemDto.CreateRequest request);
    List<ScheduleItemDto.Response> getScheduleItems(Long scheduleId);
    void deleteScheduleItem(Long scheduleId, Long itemId);
    void createBulk(Long scheduleId, List<ScheduleItemDto.CreateRequest> requests);
    void updateBulk(Long scheduleId, List<ScheduleItemDto.CreateRequest> requests);
}


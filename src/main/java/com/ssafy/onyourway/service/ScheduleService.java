package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.dto.ScheduleDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ScheduleService {
    Long createSchedule(ScheduleDto.CreateRequest request, HttpServletRequest httpServletRequest);
    void updateSchedule(Long id, ScheduleDto.UpdateRequest request);
    List<ScheduleDto.Response> getMySchedules(Long userId);
    List<ScheduleDto.Response> getMySchedulesPaged(Long userId, int page, int size);
    ScheduleDto.Response getSchedule(Long id);
    void deleteSchedule(Long id);
    void setAlarmTime(Long scheduleId, String time);

    ScheduleDto.FullResponse getFullSchedule(Long scheduleId);
}


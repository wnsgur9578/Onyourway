package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.domain.ScheduleItem;
import com.ssafy.onyourway.domain.User;
import com.ssafy.onyourway.dto.ScheduleDto;
import com.ssafy.onyourway.repository.ScheduleItemRepository;
import com.ssafy.onyourway.repository.ScheduleRepository;
import com.ssafy.onyourway.repository.UserRepository;
import com.ssafy.onyourway.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    private final JwtUtil jwtUtil;

    @Override
    public Long createSchedule(ScheduleDto.CreateRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        String token = authHeader.substring(7);
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .user(user)
                .build();

        return scheduleRepository.save(schedule).getId();
    }

    @Override
    public void updateSchedule(Long id, ScheduleDto.UpdateRequest request) {
        Optional<Schedule> optional = scheduleRepository.findById(id);
        if (optional.isPresent()) {
            Schedule schedule = optional.get();
            schedule.setTitle(request.getTitle());
            schedule.setStartDate(request.getStartDate());
            schedule.setEndDate(request.getEndDate());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public List<ScheduleDto.Response> getMySchedules(Long userId) {
        System.out.println("USER ID : " + userId);

        List<Schedule> schedules = scheduleRepository.findByUserId(userId);
        return schedules.stream()
                .map(schedule -> ScheduleDto.Response.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .startDate(schedule.getStartDate())
                        .endDate(schedule.getEndDate())
                        .build())
                .toList();
    }

    @Override
    public ScheduleDto.Response getSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정 없음"));
        return ScheduleDto.Response.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .build();
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public ScheduleDto.FullResponse getFullSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정이 존재하지 않습니다."));

        List<ScheduleItem> items = scheduleItemRepository.findByScheduleId(scheduleId);
        List<ScheduleDto.ItemResponse> itemResponses = items.stream()
                .map(ScheduleDto.ItemResponse::from)
                .toList();

        return ScheduleDto.FullResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .items(itemResponses)
                .build();
    }

    @Override
    public List<ScheduleDto.Response> getMySchedulesPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Schedule> schedules = scheduleRepository.findByUserIdWithPaging(userId, pageable);

        return schedules.stream()
                .map(schedule -> ScheduleDto.Response.builder()
                        .id(schedule.getId())
                        .title(schedule.getTitle())
                        .startDate(schedule.getStartDate())
                        .endDate(schedule.getEndDate())
                        .build())
                .toList();
    }

    @Override
    public void setAlarmTime(Long scheduleId, String timeStr) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 일정이 존재하지 않습니다."));

        schedule.setAlarmTime(LocalTime.parse(timeStr));
        scheduleRepository.save(schedule);
    }
}

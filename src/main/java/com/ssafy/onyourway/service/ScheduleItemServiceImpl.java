package com.ssafy.onyourway.service;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.domain.ScheduleItem;
import com.ssafy.onyourway.dto.ScheduleItemDto;
import com.ssafy.onyourway.repository.ScheduleItemRepository;
import com.ssafy.onyourway.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleItemServiceImpl implements ScheduleItemService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;

    @Override
    public Long addScheduleItem(Long scheduleId, ScheduleItemDto.CreateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        ScheduleItem item = ScheduleItem.builder()
                .schedule(schedule)
                .day(request.getDay())
                .time(request.getTime())
                .title(request.getTitle())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .memo(request.getMemo())
                .build();

        return scheduleItemRepository.save(item).getId();
    }

    @Override
    public void updateScheduleItem(Long scheduleId, Long itemId, ScheduleItemDto.CreateRequest request) {
        ScheduleItem item = scheduleItemRepository.findById(itemId)
                .filter(i -> i.getSchedule().getId().equals(scheduleId))
                .orElseThrow();

        item.setDay(request.getDay());
        item.setTime(request.getTime());
        item.setTitle(request.getTitle());
        item.setLocationName(request.getLocationName());
        item.setLatitude(request.getLatitude());
        item.setLongitude(request.getLongitude());
        item.setMemo(request.getMemo());

        scheduleItemRepository.save(item);
    }

    @Override
    public List<ScheduleItemDto.Response> getScheduleItems(Long scheduleId) {
        return scheduleItemRepository.findByScheduleId(scheduleId).stream()
                .map(item -> ScheduleItemDto.Response.builder()
                        .id(item.getId())
                        .day(item.getDay())
                        .time(item.getTime())
                        .title(item.getTitle())
                        .locationName(item.getLocationName())
                        .latitude(item.getLatitude())
                        .longitude(item.getLongitude())
                        .memo(item.getMemo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteScheduleItem(Long scheduleId, Long itemId) {
        ScheduleItem item = scheduleItemRepository.findById(itemId)
                .filter(i -> i.getSchedule().getId().equals(scheduleId))
                .orElseThrow();
        scheduleItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void createBulk(Long scheduleId, List<ScheduleItemDto.CreateRequest> requests) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        List<ScheduleItem> items = requests.stream()
                .map(req -> ScheduleItem.builder()
                        .schedule(schedule)
                        .day(req.getDay())
                        .time(req.getTime())
                        .title(req.getTitle())
                        .locationName(req.getLocationName())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .memo(req.getMemo())
                        .build())
                .collect(Collectors.toList());

        scheduleItemRepository.saveAll(items);
    }

    @Override
    @Transactional
    public void updateBulk(Long scheduleId, List<ScheduleItemDto.CreateRequest> requests) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        scheduleItemRepository.deleteByScheduleId(scheduleId);

        List<ScheduleItem> items = requests.stream()
                .map(req -> ScheduleItem.builder()
                        .schedule(schedule)
                        .day(req.getDay())
                        .time(req.getTime())
                        .title(req.getTitle())
                        .locationName(req.getLocationName())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .memo(req.getMemo())
                        .build())
                .collect(Collectors.toList());

        scheduleItemRepository.saveAll(items);
    }
}

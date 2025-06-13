package com.ssafy.onyourway.repository;

import com.ssafy.onyourway.domain.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findByScheduleId(Long scheduleId);
    void deleteByScheduleId(Long scheduleId);
    List<ScheduleItem> findByScheduleIdAndDay(Long scheduleId, LocalDate day);
}

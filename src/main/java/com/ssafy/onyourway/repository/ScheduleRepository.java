package com.ssafy.onyourway.repository;

import com.ssafy.onyourway.domain.Schedule;
import com.ssafy.onyourway.dto.ScheduleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserId(Long userId);

    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId ORDER BY s.startDate ASC")
    List<Schedule> findByUserIdWithPaging(@Param("userId") Long userId, Pageable pageable);

    List<Schedule> findByAlarmTime(LocalTime alarmTime);
}

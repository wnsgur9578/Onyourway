package com.ssafy.onyourway.repository;

import com.ssafy.onyourway.domain.AutoMessageToggle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface AutoMessageToggleRepository extends JpaRepository<AutoMessageToggle, Long> {

    @EntityGraph(attributePaths = {"user", "schedule"})
    Optional<AutoMessageToggle> findByUserIdAndScheduleId(Long userId, Long scheduleId);

    @EntityGraph(attributePaths = {"user", "schedule"})
    List<AutoMessageToggle> findAllByEnabledTrue();
}

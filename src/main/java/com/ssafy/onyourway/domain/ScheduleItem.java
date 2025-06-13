package com.ssafy.onyourway.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

// ScheduleItem.java (Entity)
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    private LocalDate day;
    private LocalTime time;
    private String title;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String memo;
}


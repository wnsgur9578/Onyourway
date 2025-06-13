// ✅ AutoMessageToggle.java (Entity)
package com.ssafy.onyourway.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AutoMessageToggle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    private boolean enabled;

    private String time;

    @Column(nullable = true)
    private LocalTime alarmTime;

    public void setTime(String time) {
        this.time = time;
        if (time != null && !time.isBlank()) {
            this.alarmTime = LocalTime.parse(time).withSecond(0).withNano(0);
        }
    }

    @PrePersist
    @PreUpdate
    private void validateAlarmTime() {
        if (this.alarmTime == null && this.time != null) {
            this.alarmTime = LocalTime.parse(time).withSecond(0).withNano(0);
        }
    }
}

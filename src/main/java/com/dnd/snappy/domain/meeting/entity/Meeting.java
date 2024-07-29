package com.dnd.snappy.domain.meeting.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Meeting extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private String description;

    @Column
    private String thumbnailUrl;

    @Column(nullable = false)
    private String symbolColor;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String adminPassword;
}

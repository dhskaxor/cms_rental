package com.nt.cms.rental.room.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RentalRoomVO {

    private Long id;
    private Long placeId;
    private String name;
    private String description;
    private Integer capacity;
    private Integer defaultDurationMinutes;

    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean deleted;
}


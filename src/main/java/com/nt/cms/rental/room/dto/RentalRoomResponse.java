package com.nt.cms.rental.room.dto;

import com.nt.cms.file.dto.FileResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RentalRoomResponse {

    private Long id;
    private Long placeId;
    private String name;
    private String description;
    private Integer capacity;
    private Integer defaultDurationMinutes;

    private Long basePrice;
    private Long weekendPrice;
    private String weekendApplyTo;

    private List<FileResponse> photos;
}


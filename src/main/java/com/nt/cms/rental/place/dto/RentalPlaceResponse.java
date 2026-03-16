package com.nt.cms.rental.place.dto;

import com.nt.cms.file.dto.FileResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RentalPlaceResponse {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String timeZone;
    private String openingTime;
    private String closingTime;

    private List<FileResponse> photos;
}


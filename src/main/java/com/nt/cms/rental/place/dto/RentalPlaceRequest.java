package com.nt.cms.rental.place.dto;

import lombok.Data;

@Data
public class RentalPlaceRequest {

    private String name;
    private String address;
    private String description;
    private String timeZone;
    private String openingTime;
    private String closingTime;
}


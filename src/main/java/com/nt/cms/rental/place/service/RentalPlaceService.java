package com.nt.cms.rental.place.service;

import com.nt.cms.rental.place.dto.RentalPlaceRequest;
import com.nt.cms.rental.place.dto.RentalPlaceResponse;

import java.util.List;

public interface RentalPlaceService {

    Long createPlace(RentalPlaceRequest request, Long actorId);

    void updatePlace(Long id, RentalPlaceRequest request, Long actorId);

    RentalPlaceResponse getPlace(Long id);

    List<RentalPlaceResponse> getPlaces();
}


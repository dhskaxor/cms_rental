package com.nt.cms.rental.room.service;

import com.nt.cms.rental.room.dto.RentalRoomRequest;
import com.nt.cms.rental.room.dto.RentalRoomResponse;

import java.util.List;

public interface RentalRoomService {

    Long createRoom(RentalRoomRequest request, Long actorId);

    void updateRoom(Long id, RentalRoomRequest request, Long actorId);

    RentalRoomResponse getRoom(Long id);

    List<RentalRoomResponse> getRoomsByPlace(Long placeId);

    List<RentalRoomResponse> getRooms();
}


package com.nt.cms.rental.room.service;

import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotRequest;
import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotResponse;

import java.util.List;

public interface RentalUnavailableSlotService {

    List<RentalRoomUnavailableSlotResponse> getSlots(Long roomId, String fromDateTime, String toDateTime);

    void createSlot(Long roomId, RentalRoomUnavailableSlotRequest request, Long actorId);

    void updateSlot(Long id, Long roomId, RentalRoomUnavailableSlotRequest request, Long actorId);

    void deleteSlot(Long id, Long roomId, Long actorId);
}


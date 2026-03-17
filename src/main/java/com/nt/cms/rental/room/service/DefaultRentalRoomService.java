package com.nt.cms.rental.room.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import com.nt.cms.rental.pricing.dto.RoomBasePricingResponse;
import com.nt.cms.rental.pricing.dto.RoomWeekendHolidayPricingResponse;
import com.nt.cms.rental.pricing.mapper.RentalPricingMapper;
import com.nt.cms.rental.room.dto.RentalRoomRequest;
import com.nt.cms.rental.room.dto.RentalRoomResponse;
import com.nt.cms.rental.room.mapper.RentalRoomMapper;
import com.nt.cms.rental.room.vo.RentalRoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalRoomService implements RentalRoomService {

    private static final String FILE_REF_TYPE_ROOM = "RENTAL_ROOM";

    private static final int PRICING_UNIT_MINUTES = 60;
    private static final String DEFAULT_WEEKEND_APPLY_TO = "BOTH";

    private final RentalRoomMapper rentalRoomMapper;
    private final FileService fileService;
    private final RentalPricingMapper rentalPricingMapper;

    @Override
    @Transactional
    public Long createRoom(RentalRoomRequest request, Long actorId) {
        LocalDateTime now = LocalDateTime.now();

        int capacity = request.getCapacity() != null ? request.getCapacity() : 5;
        int durationMinutes = request.getDefaultDurationMinutes() != null ? request.getDefaultDurationMinutes() : 60;
        RentalRoomVO vo = RentalRoomVO.builder()
                .placeId(request.getPlaceId())
                .name(request.getName())
                .description(request.getDescription())
                .capacity(capacity)
                .defaultDurationMinutes(durationMinutes)
                .createdAt(now)
                .createdBy(actorId)
                .updatedAt(now)
                .updatedBy(actorId)
                .deleted(false)
                .build();

        rentalRoomMapper.insert(vo);
        Long roomId = vo.getId();
        if (request.getBasePrice() != null) {
            rentalPricingMapper.upsertBasePricing(roomId, PRICING_UNIT_MINUTES, request.getBasePrice());
        }
        if (request.getWeekendPrice() != null) {
            String applyTo = request.getWeekendApplyTo() != null && !request.getWeekendApplyTo().isBlank()
                    ? request.getWeekendApplyTo() : DEFAULT_WEEKEND_APPLY_TO;
            int updated = rentalPricingMapper.updateWeekendHolidayPricing(roomId, applyTo, PRICING_UNIT_MINUTES, request.getWeekendPrice());
            if (updated == 0) {
                rentalPricingMapper.insertWeekendHolidayPricing(roomId, applyTo, PRICING_UNIT_MINUTES, request.getWeekendPrice());
            }
        }
        return roomId;
    }

    @Override
    @Transactional
    public void updateRoom(Long id, RentalRoomRequest request, Long actorId) {
        RentalRoomVO existing = rentalRoomMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        existing.setPlaceId(request.getPlaceId());
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCapacity(request.getCapacity() != null ? request.getCapacity() : 5);
        existing.setDefaultDurationMinutes(request.getDefaultDurationMinutes() != null ? request.getDefaultDurationMinutes() : 60);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(actorId);

        rentalRoomMapper.update(existing);
        if (request.getBasePrice() != null) {
            rentalPricingMapper.upsertBasePricing(id, PRICING_UNIT_MINUTES, request.getBasePrice());
        }
        if (request.getWeekendPrice() != null) {
            String applyTo = request.getWeekendApplyTo() != null && !request.getWeekendApplyTo().isBlank()
                    ? request.getWeekendApplyTo() : DEFAULT_WEEKEND_APPLY_TO;
            int updated = rentalPricingMapper.updateWeekendHolidayPricing(id, applyTo, PRICING_UNIT_MINUTES, request.getWeekendPrice());
            if (updated == 0) {
                rentalPricingMapper.insertWeekendHolidayPricing(id, applyTo, PRICING_UNIT_MINUTES, request.getWeekendPrice());
            }
        }
    }

    @Override
    public RentalRoomResponse getRoom(Long id) {
        RentalRoomVO vo = rentalRoomMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, id);
        RoomBasePricingResponse base = rentalPricingMapper.findBasePricingByRoomId(id);
        RoomWeekendHolidayPricingResponse weekend = rentalPricingMapper.findWeekendHolidayPricingByRoomId(id);
        return toResponse(vo, photos, base, weekend);
    }

    @Override
    public List<RentalRoomResponse> getRoomsByPlace(Long placeId) {
        List<RentalRoomVO> list = rentalRoomMapper.findByPlaceId(placeId);
        return list.stream()
                .map(vo -> {
                    List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, vo.getId());
                    RoomBasePricingResponse base = rentalPricingMapper.findBasePricingByRoomId(vo.getId());
                    RoomWeekendHolidayPricingResponse weekend = rentalPricingMapper.findWeekendHolidayPricingByRoomId(vo.getId());
                    return toResponse(vo, photos, base, weekend);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalRoomResponse> getRooms() {
        List<RentalRoomVO> list = rentalRoomMapper.findAll();
        return list.stream()
                .map(vo -> {
                    List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, vo.getId());
                    RoomBasePricingResponse base = rentalPricingMapper.findBasePricingByRoomId(vo.getId());
                    RoomWeekendHolidayPricingResponse weekend = rentalPricingMapper.findWeekendHolidayPricingByRoomId(vo.getId());
                    return toResponse(vo, photos, base, weekend);
                })
                .collect(Collectors.toList());
    }

    private RentalRoomResponse toResponse(RentalRoomVO vo, List<FileResponse> photos,
                                          RoomBasePricingResponse base, RoomWeekendHolidayPricingResponse weekend) {
        return RentalRoomResponse.builder()
                .id(vo.getId())
                .placeId(vo.getPlaceId())
                .name(vo.getName())
                .description(vo.getDescription())
                .capacity(vo.getCapacity())
                .defaultDurationMinutes(vo.getDefaultDurationMinutes())
                .basePrice(base != null ? base.getPrice() : null)
                .weekendPrice(weekend != null ? weekend.getPrice() : null)
                .weekendApplyTo(weekend != null ? weekend.getApplyTo() : null)
                .photos(photos)
                .build();
    }
}


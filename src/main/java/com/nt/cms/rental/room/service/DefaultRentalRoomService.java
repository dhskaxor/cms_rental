package com.nt.cms.rental.room.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
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

    private final RentalRoomMapper rentalRoomMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public Long createRoom(RentalRoomRequest request, Long actorId) {
        LocalDateTime now = LocalDateTime.now();

        RentalRoomVO vo = RentalRoomVO.builder()
                .placeId(request.getPlaceId())
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .defaultDurationMinutes(request.getDefaultDurationMinutes())
                .createdAt(now)
                .createdBy(actorId)
                .updatedAt(now)
                .updatedBy(actorId)
                .deleted(false)
                .build();

        rentalRoomMapper.insert(vo);
        return vo.getId();
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
        existing.setCapacity(request.getCapacity());
        existing.setDefaultDurationMinutes(request.getDefaultDurationMinutes());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(actorId);

        rentalRoomMapper.update(existing);
    }

    @Override
    public RentalRoomResponse getRoom(Long id) {
        RentalRoomVO vo = rentalRoomMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, id);
        return toResponse(vo, photos);
    }

    @Override
    public List<RentalRoomResponse> getRoomsByPlace(Long placeId) {
        List<RentalRoomVO> list = rentalRoomMapper.findByPlaceId(placeId);
        return list.stream()
                .map(vo -> {
                    List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, vo.getId());
                    return toResponse(vo, photos);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalRoomResponse> getRooms() {
        List<RentalRoomVO> list = rentalRoomMapper.findAll();
        return list.stream()
                .map(vo -> {
                    List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_ROOM, vo.getId());
                    return toResponse(vo, photos);
                })
                .collect(Collectors.toList());
    }

    private RentalRoomResponse toResponse(RentalRoomVO vo, List<FileResponse> photos) {
        return RentalRoomResponse.builder()
                .id(vo.getId())
                .placeId(vo.getPlaceId())
                .name(vo.getName())
                .description(vo.getDescription())
                .capacity(vo.getCapacity())
                .defaultDurationMinutes(vo.getDefaultDurationMinutes())
                .photos(photos)
                .build();
    }
}


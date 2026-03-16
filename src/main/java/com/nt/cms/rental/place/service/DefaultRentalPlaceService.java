package com.nt.cms.rental.place.service;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import com.nt.cms.rental.place.dto.RentalPlaceRequest;
import com.nt.cms.rental.place.dto.RentalPlaceResponse;
import com.nt.cms.rental.place.mapper.RentalPlaceMapper;
import com.nt.cms.rental.place.vo.RentalPlaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRentalPlaceService implements RentalPlaceService {

    private static final String FILE_REF_TYPE_PLACE = "RENTAL_PLACE";

    private final RentalPlaceMapper rentalPlaceMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public Long createPlace(RentalPlaceRequest request, Long actorId) {
        LocalDateTime now = LocalDateTime.now();

        RentalPlaceVO vo = RentalPlaceVO.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .timeZone(request.getTimeZone())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .createdAt(now)
                .createdBy(actorId)
                .updatedAt(now)
                .updatedBy(actorId)
                .deleted(false)
                .build();

        rentalPlaceMapper.insert(vo);
        return vo.getId();
    }

    @Override
    @Transactional
    public void updatePlace(Long id, RentalPlaceRequest request, Long actorId) {
        RentalPlaceVO existing = rentalPlaceMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setDescription(request.getDescription());
        existing.setTimeZone(request.getTimeZone());
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(actorId);

        rentalPlaceMapper.update(existing);
    }

    @Override
    public RentalPlaceResponse getPlace(Long id) {
        RentalPlaceVO vo = rentalPlaceMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_PLACE, id);
        return toResponse(vo, photos);
    }

    @Override
    public List<RentalPlaceResponse> getPlaces() {
        List<RentalPlaceVO> list = rentalPlaceMapper.findAll();
        return list.stream()
                .map(vo -> {
                    List<FileResponse> photos = fileService.getFilesByRef(FILE_REF_TYPE_PLACE, vo.getId());
                    return toResponse(vo, photos);
                })
                .collect(Collectors.toList());
    }

    private RentalPlaceResponse toResponse(RentalPlaceVO vo, List<FileResponse> photos) {
        return RentalPlaceResponse.builder()
                .id(vo.getId())
                .name(vo.getName())
                .address(vo.getAddress())
                .description(vo.getDescription())
                .timeZone(vo.getTimeZone())
                .openingTime(vo.getOpeningTime())
                .closingTime(vo.getClosingTime())
                .photos(photos)
                .build();
    }
}


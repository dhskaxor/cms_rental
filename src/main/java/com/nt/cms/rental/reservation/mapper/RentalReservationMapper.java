package com.nt.cms.rental.reservation.mapper;

import com.nt.cms.rental.reservation.dto.RentalReservationSearchRequest;
import com.nt.cms.rental.reservation.vo.RentalReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RentalReservationMapper {

    void insert(RentalReservationVO reservation);

    void update(RentalReservationVO reservation);

    RentalReservationVO findById(@Param("id") Long id);

    RentalReservationVO findByIdWithUserName(@Param("id") Long id);

    List<RentalReservationVO> findByUserId(@Param("userId") Long userId);

    List<RentalReservationVO> search(@Param("request") RentalReservationSearchRequest request);

    List<RentalReservationVO> findOverlappingReservations(@Param("roomId") Long roomId,
                                                          @Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    Long sumTotalPriceBySearch(@Param("request") RentalReservationSearchRequest request);
}


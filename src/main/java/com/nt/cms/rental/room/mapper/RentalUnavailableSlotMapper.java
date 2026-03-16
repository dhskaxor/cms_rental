package com.nt.cms.rental.room.mapper;

import com.nt.cms.rental.room.dto.RentalRoomUnavailableSlotResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalUnavailableSlotMapper {

    List<RentalRoomUnavailableSlotResponse> findByRoomIdAndRange(@Param("roomId") Long roomId,
                                                                 @Param("fromDateTime") String fromDateTime,
                                                                 @Param("toDateTime") String toDateTime);

    void insert(@Param("roomId") Long roomId,
                @Param("scopeType") String scopeType,
                @Param("startDateTime") String startDateTime,
                @Param("endDateTime") String endDateTime,
                @Param("weekDay") Integer weekDay,
                @Param("startTime") String startTime,
                @Param("endTime") String endTime,
                @Param("reason") String reason,
                @Param("actorId") Long actorId);

    void update(@Param("id") Long id,
                @Param("roomId") Long roomId,
                @Param("scopeType") String scopeType,
                @Param("startDateTime") String startDateTime,
                @Param("endDateTime") String endDateTime,
                @Param("weekDay") Integer weekDay,
                @Param("startTime") String startTime,
                @Param("endTime") String endTime,
                @Param("reason") String reason,
                @Param("actorId") Long actorId);

    void softDelete(@Param("id") Long id,
                    @Param("roomId") Long roomId,
                    @Param("actorId") Long actorId);
}


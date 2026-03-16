package com.nt.cms.rental.calendar.mapper;

import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleRequest;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalClosedRuleMapper {

    List<RentalPlaceClosedRuleResponse> findByPlaceIdAndRange(@Param("placeId") Long placeId,
                                                              @Param("fromDate") String fromDate,
                                                              @Param("toDate") String toDate);

    void insert(@Param("placeId") Long placeId,
                @Param("rule") RentalPlaceClosedRuleRequest request,
                @Param("actorId") Long actorId);

    void update(@Param("id") Long id,
                @Param("placeId") Long placeId,
                @Param("rule") RentalPlaceClosedRuleRequest request,
                @Param("actorId") Long actorId);

    void softDelete(@Param("id") Long id,
                    @Param("placeId") Long placeId,
                    @Param("actorId") Long actorId);
}


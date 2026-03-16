package com.nt.cms.rental.room.mapper;

import com.nt.cms.rental.room.vo.RentalRoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalRoomMapper {

    void insert(RentalRoomVO room);

    void update(RentalRoomVO room);

    RentalRoomVO findById(@Param("id") Long id);

    List<RentalRoomVO> findByPlaceId(@Param("placeId") Long placeId);

    List<RentalRoomVO> findAll();
}


package com.nt.cms.rental.place.mapper;

import com.nt.cms.rental.place.vo.RentalPlaceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalPlaceMapper {

    void insert(RentalPlaceVO place);

    void update(RentalPlaceVO place);

    RentalPlaceVO findById(@Param("id") Long id);

    List<RentalPlaceVO> findAll();
}


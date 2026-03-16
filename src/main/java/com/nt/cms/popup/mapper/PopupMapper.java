package com.nt.cms.popup.mapper;

import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.vo.SitePopupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 팝업 Mapper
 *
 * @author CMS Team
 */
@Mapper
public interface PopupMapper {

    List<SitePopupVO> findAll(PopupSearchRequest request);

    long count(PopupSearchRequest request);

    SitePopupVO findById(@Param("id") Long id);

    SitePopupVO findByPopupCode(@Param("popupCode") String popupCode);

    boolean existsByPopupCode(@Param("popupCode") String popupCode);

    List<SitePopupVO> findDisplayTarget(@Param("positionType") String positionType,
                                        @Param("deviceType") String deviceType,
                                        @Param("isLogin") boolean isLogin);

    int insert(SitePopupVO vo);

    int update(SitePopupVO vo);

    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);
}

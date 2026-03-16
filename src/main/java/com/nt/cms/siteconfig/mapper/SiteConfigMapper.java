package com.nt.cms.siteconfig.mapper;

import com.nt.cms.siteconfig.vo.SiteConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사이트 설정 Mapper 인터페이스
 *
 * @author CMS Team
 */
@Mapper
public interface SiteConfigMapper {

    /**
     * ID로 사이트 설정 조회 (id=1 단일 행)
     *
     * @param id 사이트 설정 ID (항상 1)
     * @return 사이트 설정 VO
     */
    SiteConfigVO findById(@Param("id") Long id);

    /**
     * 사이트 설정 수정
     *
     * @param vo 사이트 설정 VO
     * @return 업데이트된 행 수
     */
    int update(SiteConfigVO vo);

    /**
     * 사이트 설정 초기 행 삽입
     *
     * @param vo 사이트 설정 VO
     * @return 삽입된 행 수
     */
    int insert(SiteConfigVO vo);
}

package com.nt.cms.commoncode.mapper;

import com.nt.cms.commoncode.vo.CommonCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공통 코드 Mapper
 *
 * @author CMS Team
 */
@Mapper
public interface CommonCodeMapper {

    /**
     * 그룹별 코드 목록 조회 (평면)
     */
    List<CommonCodeVO> findByGroupId(@Param("groupId") Long groupId);

    /**
     * 그룹별 코드 목록 조회 (계층, 최상위만)
     */
    List<CommonCodeVO> findRootByGroupId(@Param("groupId") Long groupId);

    /**
     * 하위 코드 목록 조회
     */
    List<CommonCodeVO> findChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * ID로 코드 조회
     */
    CommonCodeVO findById(@Param("id") Long id);

    /**
     * 그룹 내 코드 존재 여부 (group_id, code)
     */
    boolean existsByGroupIdAndCode(@Param("groupId") Long groupId, @Param("code") String code,
                                   @Param("excludeId") Long excludeId);

    /**
     * 하위 코드 수 조회
     */
    int countChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 코드 등록
     */
    int insert(CommonCodeVO vo);

    /**
     * 코드 수정
     */
    int update(CommonCodeVO vo);

    /**
     * 코드 삭제 (Soft Delete)
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 그룹 코드로 활성 코드 목록 조회 (API용, 기간 조건 적용)
     */
    List<CommonCodeVO> findActiveByGroupCode(@Param("groupCode") String groupCode);
}

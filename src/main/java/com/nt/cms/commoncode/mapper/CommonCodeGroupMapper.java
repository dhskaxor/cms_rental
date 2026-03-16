package com.nt.cms.commoncode.mapper;

import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공통 코드 그룹 Mapper
 *
 * @author CMS Team
 */
@Mapper
public interface CommonCodeGroupMapper {

    List<CommonCodeGroupVO> findAll();

    CommonCodeGroupVO findById(@Param("id") Long id);

    CommonCodeGroupVO findByGroupCode(@Param("groupCode") String groupCode);

    boolean existsByGroupCode(@Param("groupCode") String groupCode);

    int countCodesByGroupId(@Param("groupId") Long groupId);

    int insert(CommonCodeGroupVO vo);

    int update(CommonCodeGroupVO vo);

    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);
}

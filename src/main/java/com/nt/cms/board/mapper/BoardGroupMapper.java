package com.nt.cms.board.mapper;

import com.nt.cms.board.vo.BoardGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 게시판 그룹 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface BoardGroupMapper {

    /**
     * 그룹 목록 조회
     */
    List<BoardGroupVO> findAll();

    /**
     * ID로 그룹 조회
     */
    BoardGroupVO findById(@Param("id") Long id);

    /**
     * 그룹 코드로 조회
     */
    BoardGroupVO findByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 그룹 코드 존재 여부
     */
    boolean existsByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 그룹 등록
     */
    int insert(BoardGroupVO group);

    /**
     * 그룹 수정
     */
    int update(BoardGroupVO group);

    /**
     * 그룹 삭제 (Soft Delete)
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);
}

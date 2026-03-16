package com.nt.cms.board.mapper;

import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 게시판 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface BoardMapper {

    /**
     * 게시판 목록 조회
     */
    List<BoardVO> findAll();

    /**
     * 그룹별 게시판 목록 조회
     */
    List<BoardVO> findByGroupId(@Param("groupId") Long groupId);

    /**
     * ID로 게시판 조회
     */
    BoardVO findById(@Param("id") Long id);

    /**
     * 게시판 코드로 조회
     */
    BoardVO findByBoardCode(@Param("boardCode") String boardCode);

    /**
     * 게시판 코드 존재 여부
     */
    boolean existsByBoardCode(@Param("boardCode") String boardCode);

    /**
     * 그룹에 속한 게시판 수 조회
     */
    int countByGroupId(@Param("groupId") Long groupId);

    /**
     * 게시판 등록
     */
    int insert(BoardVO board);

    /**
     * 게시판 수정
     */
    int update(BoardVO board);

    /**
     * 게시판 삭제 (Soft Delete)
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 게시판 권한 목록 조회
     */
    List<BoardPermissionVO> findPermissionsByBoardId(@Param("boardId") Long boardId);

    /**
     * 게시판 권한 삭제
     */
    int deletePermissions(@Param("boardId") Long boardId);

    /**
     * 게시판 권한 등록
     */
    int insertPermission(BoardPermissionVO permission);

    /**
     * 사용자 역할로 게시판 권한 조회
     */
    BoardPermissionVO findPermissionByBoardIdAndRoleId(@Param("boardId") Long boardId, @Param("roleId") Long roleId);
}

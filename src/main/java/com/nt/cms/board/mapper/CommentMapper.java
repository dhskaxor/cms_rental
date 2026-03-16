package com.nt.cms.board.mapper;

import com.nt.cms.board.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 댓글 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface CommentMapper {

    /**
     * 게시글별 댓글 목록 조회 (최상위 댓글만)
     */
    List<CommentVO> findByPostId(@Param("postId") Long postId);

    /**
     * 대댓글 목록 조회
     */
    List<CommentVO> findRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * ID로 댓글 조회
     */
    CommentVO findById(@Param("id") Long id);

    /**
     * 댓글 등록
     */
    int insert(CommentVO comment);

    /**
     * 댓글 수정
     */
    int update(CommentVO comment);

    /**
     * 댓글 삭제 (Soft Delete)
     */
    int delete(@Param("id") Long id);

    /**
     * 게시글별 댓글 수 조회
     */
    int countByPostId(@Param("postId") Long postId);

    /**
     * 대댓글 존재 여부
     */
    boolean hasReplies(@Param("parentId") Long parentId);
}

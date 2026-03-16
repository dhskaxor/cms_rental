package com.nt.cms.board.mapper;

import com.nt.cms.board.dto.PostSearchRequest;
import com.nt.cms.board.vo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 게시글 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface PostMapper {

    /**
     * 게시글 목록 조회 (페이징)
     */
    List<PostVO> findByBoardId(@Param("boardId") Long boardId, @Param("request") PostSearchRequest request);

    /**
     * 게시글 총 개수
     */
    long countByBoardId(@Param("boardId") Long boardId, @Param("request") PostSearchRequest request);

    /**
     * 공지글 목록 조회
     */
    List<PostVO> findNoticesByBoardId(@Param("boardId") Long boardId);

    /**
     * ID로 게시글 조회
     */
    PostVO findById(@Param("id") Long id);

    /**
     * 게시글 등록
     */
    int insert(PostVO post);

    /**
     * 게시글 수정
     */
    int update(PostVO post);

    /**
     * 게시글 삭제 (Soft Delete)
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 조회수 증가
     */
    int increaseViewCount(@Param("id") Long id);

    /**
     * 이전 게시글 조회
     */
    PostVO findPrevPost(@Param("boardId") Long boardId, @Param("id") Long id);

    /**
     * 다음 게시글 조회
     */
    PostVO findNextPost(@Param("boardId") Long boardId, @Param("id") Long id);

    /**
     * 최신글 목록 조회 (여러 게시판)
     * 
     * @param boardIds 게시판 ID 목록
     * @param size 조회할 개수
     * @param sortField 정렬 필드 (created_at, view_count, title)
     * @param sortOrder 정렬 방식 (ASC, DESC)
     * @return 최신글 목록
     */
    List<PostVO> findLatestPosts(
            @Param("boardIds") List<Long> boardIds,
            @Param("size") int size,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder);
}

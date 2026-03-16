package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;
import com.nt.cms.common.response.PageResponse;

import java.util.List;

/**
 * 게시글 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface PostService {

    /**
     * 게시글 목록 조회 (페이징)
     */
    PageResponse<PostResponse> getPosts(Long boardId, PostSearchRequest request);

    /**
     * 공지글 목록 조회
     */
    List<PostResponse> getNotices(Long boardId);

    /**
     * 게시글 상세 조회
     */
    PostResponse getPost(Long id, Long userId, Long roleId);

    /**
     * 게시글 생성
     */
    PostResponse createPost(Long boardId, PostCreateRequest request, Long userId, Long roleId);

    /**
     * 게시글 수정
     */
    PostResponse updatePost(Long id, PostUpdateRequest request, Long userId, Long roleId);

    /**
     * 게시글 삭제
     */
    void deletePost(Long id, Long userId, Long roleId);

    /**
     * 이전/다음 게시글 조회
     */
    PostResponse getPrevPost(Long boardId, Long id);
    PostResponse getNextPost(Long boardId, Long id);

    /**
     * 최신글 목록 조회 (여러 게시판)
     * 
     * @param request 최신글 조회 요청 (boardIds, size, sortField, sortOrder)
     * @return 최신글 목록
     */
    List<PostResponse> getLatestPosts(LatestPostsRequest request);
}

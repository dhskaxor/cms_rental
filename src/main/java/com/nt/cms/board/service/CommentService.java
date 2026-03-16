package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;

import java.util.List;

/**
 * 댓글 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface CommentService {

    /**
     * 게시글별 댓글 목록 조회 (대댓글 포함)
     */
    List<CommentResponse> getComments(Long postId);

    /**
     * 댓글 생성
     */
    CommentResponse createComment(Long postId, CommentCreateRequest request, Long userId);

    /**
     * 댓글 수정
     */
    CommentResponse updateComment(Long id, CommentUpdateRequest request, Long userId);

    /**
     * 댓글 삭제
     */
    void deleteComment(Long id, Long userId, Long roleId, Long boardId);
}

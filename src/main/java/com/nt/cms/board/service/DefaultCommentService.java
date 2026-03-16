package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.board.mapper.CommentMapper;
import com.nt.cms.board.mapper.PostMapper;
import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
import com.nt.cms.board.vo.CommentVO;
import com.nt.cms.board.vo.PostVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultCommentService implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final BoardMapper boardMapper;

    @Override
    public List<CommentResponse> getComments(Long postId) {
        log.debug("댓글 목록 조회: postId={}", postId);

        // 최상위 댓글 조회
        List<CommentVO> comments = commentMapper.findByPostId(postId);

        // 대댓글 포함
        for (CommentVO comment : comments) {
            List<CommentVO> replies = commentMapper.findRepliesByParentId(comment.getId());
            comment.setReplies(replies);
        }

        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long userId) {
        log.info("댓글 생성: postId={}", postId);

        // 게시글 존재 확인
        PostVO post = postMapper.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 게시판 댓글 사용 여부 확인
        BoardVO board = boardMapper.findById(post.getBoardId());
        if (board == null || !Boolean.TRUE.equals(board.getUseComment())) {
            throw new BusinessException(ErrorCode.COMMENT_DISABLED);
        }

        // 대댓글인 경우 부모 댓글 확인 및 depth 체크
        if (request.getParentId() != null) {
            CommentVO parentComment = commentMapper.findById(request.getParentId());
            if (parentComment == null || parentComment.isDeleted()) {
                throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
            }
            // 대댓글은 1단계까지만 허용 (부모가 이미 대댓글이면 불가)
            if (parentComment.getParentId() != null) {
                throw new BusinessException(ErrorCode.COMMENT_DEPTH_EXCEEDED);
            }
        }

        // 댓글 생성
        CommentVO comment = CommentVO.builder()
                .postId(postId)
                .parentId(request.getParentId())
                .content(request.getContent())
                .writerId(userId)
                .build();

        commentMapper.insert(comment);
        log.info("댓글 생성 완료: id={}", comment.getId());

        return CommentResponse.from(commentMapper.findById(comment.getId()));
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentUpdateRequest request, Long userId) {
        log.info("댓글 수정: id={}", id);

        CommentVO comment = commentMapper.findById(id);
        if (comment == null || comment.isDeleted()) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        // 작성자 확인
        if (!comment.getWriterId().equals(userId)) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }

        // 댓글 수정
        CommentVO updateComment = CommentVO.builder()
                .id(id)
                .content(request.getContent())
                .build();

        commentMapper.update(updateComment);
        log.info("댓글 수정 완료: id={}", id);

        return CommentResponse.from(commentMapper.findById(id));
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId, Long roleId, Long boardId) {
        log.info("댓글 삭제: id={}", id);

        CommentVO comment = commentMapper.findById(id);
        if (comment == null || comment.isDeleted()) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        // 작성자이거나 삭제 권한이 있는지 확인
        boolean isWriter = comment.getWriterId().equals(userId);
        boolean hasDeletePermission = false;
        if (boardId != null && roleId != null) {
            BoardPermissionVO permission = boardMapper.findPermissionByBoardIdAndRoleId(boardId, roleId);
            hasDeletePermission = permission != null && Boolean.TRUE.equals(permission.getCanDelete());
        }

        if (!isWriter && !hasDeletePermission) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }

        commentMapper.delete(id);
        log.info("댓글 삭제 완료: id={}", id);
    }
}

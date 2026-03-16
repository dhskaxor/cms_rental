package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;

import java.util.List;

/**
 * 게시판 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface BoardService {

    /**
     * 게시판 목록 조회
     */
    List<BoardResponse> getBoards();

    /**
     * 그룹별 게시판 목록 조회
     */
    List<BoardResponse> getBoardsByGroupId(Long groupId);

    /**
     * 게시판 상세 조회
     */
    BoardResponse getBoard(Long id);

    /**
     * 게시판 코드로 조회
     */
    BoardResponse getBoardByCode(String boardCode);

    /**
     * 게시판 생성
     */
    BoardResponse createBoard(BoardCreateRequest request, Long createdBy);

    /**
     * 게시판 수정
     */
    BoardResponse updateBoard(Long id, BoardUpdateRequest request, Long updatedBy);

    /**
     * 게시판 삭제
     */
    void deleteBoard(Long id, Long deletedBy);

    /**
     * 게시판 코드 중복 확인
     */
    boolean isBoardCodeDuplicated(String boardCode);

    /**
     * 게시판 권한 확인
     */
    boolean hasPermission(Long boardId, Long roleId, String action);

    /**
     * 사용 가능한 게시판 템플릿 코드 목록 조회
     * (templates/site/board/ 하위 폴더명 기준, 폴더 추가 시 자동 반영)
     */
    List<String> getAvailableTemplateCodes();

    /**
     * 게시판 목록 뷰 이름 해석 (템플릿 폴더 기반)
     */
    String resolveBoardListView(String templateCode);

    /**
     * 게시글 상세 뷰 이름 해석 (템플릿 폴더 기반)
     */
    String resolvePostDetailView(String templateCode);

    /**
     * 게시글 폼 뷰 이름 해석 (템플릿 폴더 기반)
     */
    String resolvePostFormView(String templateCode);
}

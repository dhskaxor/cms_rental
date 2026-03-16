package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;

import java.util.List;

/**
 * 게시판 그룹 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface BoardGroupService {

    /**
     * 그룹 목록 조회
     */
    List<BoardGroupResponse> getGroups();

    /**
     * 그룹 상세 조회
     */
    BoardGroupResponse getGroup(Long id);

    /**
     * 그룹 생성
     */
    BoardGroupResponse createGroup(BoardGroupCreateRequest request, Long createdBy);

    /**
     * 그룹 수정
     */
    BoardGroupResponse updateGroup(Long id, BoardGroupUpdateRequest request, Long updatedBy);

    /**
     * 그룹 삭제
     */
    void deleteGroup(Long id, Long deletedBy);

    /**
     * 그룹 코드 중복 확인
     */
    boolean isGroupCodeDuplicated(String groupCode);
}

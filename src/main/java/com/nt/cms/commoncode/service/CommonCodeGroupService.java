package com.nt.cms.commoncode.service;

import com.nt.cms.commoncode.dto.CommonCodeGroupCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeGroupResponse;
import com.nt.cms.commoncode.dto.CommonCodeGroupUpdateRequest;

import java.util.List;

/**
 * 공통 코드 그룹 서비스 인터페이스
 *
 * @author CMS Team
 */
public interface CommonCodeGroupService {

    /**
     * 그룹 목록 조회
     */
    List<CommonCodeGroupResponse> getGroups();

    /**
     * 그룹 상세 조회
     */
    CommonCodeGroupResponse getGroup(Long id);

    /**
     * 그룹 코드로 조회
     */
    CommonCodeGroupResponse getGroupByCode(String groupCode);

    /**
     * 그룹 생성
     */
    CommonCodeGroupResponse createGroup(CommonCodeGroupCreateRequest request, Long createdBy);

    /**
     * 그룹 수정
     */
    CommonCodeGroupResponse updateGroup(Long id, CommonCodeGroupUpdateRequest request, Long updatedBy);

    /**
     * 그룹 삭제
     */
    void deleteGroup(Long id, Long deletedBy);

    /**
     * 그룹 코드 중복 확인
     */
    boolean isGroupCodeDuplicated(String groupCode);
}

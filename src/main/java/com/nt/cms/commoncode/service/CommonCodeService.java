package com.nt.cms.commoncode.service;

import com.nt.cms.commoncode.dto.CommonCodeCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.dto.CommonCodeUpdateRequest;

import java.util.List;

/**
 * 공통 코드 서비스 인터페이스
 *
 * @author CMS Team
 */
public interface CommonCodeService {

    /**
     * 그룹별 코드 목록 조회 (평면)
     */
    List<CommonCodeResponse> getCodesByGroupId(Long groupId);

    /**
     * 그룹별 코드 목록 조회 (계층 구조)
     */
    List<CommonCodeResponse> getCodesByGroupIdHierarchy(Long groupId);

    /**
     * 그룹 코드로 활성 코드 목록 조회 (API용)
     */
    List<CommonCodeResponse> getActiveCodesByGroupCode(String groupCode);

    /**
     * 코드 상세 조회
     */
    CommonCodeResponse getCode(Long id);

    /**
     * 코드 생성
     */
    CommonCodeResponse createCode(CommonCodeCreateRequest request, Long createdBy);

    /**
     * 코드 수정
     */
    CommonCodeResponse updateCode(Long id, CommonCodeUpdateRequest request, Long updatedBy);

    /**
     * 코드 삭제
     */
    void deleteCode(Long id, Long deletedBy);

    /**
     * 그룹 내 코드 중복 확인
     */
    boolean isCodeDuplicated(Long groupId, String code, Long excludeId);

    /**
     * 코드의 모든 자손 ID 목록 조회 (수정 시 상위 코드 선택 순환 방지용)
     */
    List<Long> findDescendantIds(Long codeId);
}

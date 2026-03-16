package com.nt.cms.audit.mapper;

import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.vo.AuditLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 감사 로그 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface AuditLogMapper {

    /**
     * 감사 로그 등록
     * 
     * @param auditLog 감사 로그 VO
     * @return 등록된 행 수
     */
    int insert(AuditLogVO auditLog);

    /**
     * ID로 감사 로그 조회
     * 
     * @param id 감사 로그 ID
     * @return 감사 로그 VO (없으면 null)
     */
    AuditLogVO findById(@Param("id") Long id);

    /**
     * 감사 로그 목록 조회 (페이징, 검색)
     * 
     * @param request 검색 조건
     * @return 감사 로그 목록
     */
    List<AuditLogVO> findAll(@Param("request") AuditLogSearchRequest request);

    /**
     * 감사 로그 총 개수
     * 
     * @param request 검색 조건
     * @return 총 개수
     */
    long countAll(@Param("request") AuditLogSearchRequest request);
}

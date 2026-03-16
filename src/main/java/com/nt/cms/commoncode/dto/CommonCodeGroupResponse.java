package com.nt.cms.commoncode.dto;

import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 코드 그룹 응답 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupResponse {

    private Long id;
    private String groupCode;
    private String groupName;
    private String description;
    private Boolean isSystem;
    private Boolean isActive;
    private Long codeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static CommonCodeGroupResponse from(CommonCodeGroupVO vo) {
        if (vo == null) {
            return null;
        }
        return CommonCodeGroupResponse.builder()
                .id(vo.getId())
                .groupCode(vo.getGroupCode())
                .groupName(vo.getGroupName())
                .description(vo.getDescription())
                .isSystem(Boolean.TRUE.equals(vo.getIsSystem()))
                .isActive(vo.getIsActive() == null || Boolean.TRUE.equals(vo.getIsActive()))
                .codeCount(vo.getCodeCount())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}

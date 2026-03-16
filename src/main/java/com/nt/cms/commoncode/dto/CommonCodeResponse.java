package com.nt.cms.commoncode.dto;

import com.nt.cms.commoncode.vo.CommonCodeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공통 코드 응답 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeResponse {

    private Long id;
    private Long groupId;
    private String groupCode;
    private Long parentId;
    private Integer depth;
    private String code;
    private String codeName;
    private String codeValue;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isSystem;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer childCount;
    private List<CommonCodeResponse> children;

    /**
     * VO에서 Response 생성 (계층 포함)
     */
    public static CommonCodeResponse from(CommonCodeVO vo) {
        if (vo == null) {
            return null;
        }
        List<CommonCodeResponse> childrenList = null;
        if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
            childrenList = vo.getChildren().stream()
                    .map(CommonCodeResponse::from)
                    .collect(Collectors.toList());
        }
        return CommonCodeResponse.builder()
                .id(vo.getId())
                .groupId(vo.getGroupId())
                .groupCode(vo.getGroupCode())
                .parentId(vo.getParentId())
                .depth(vo.getDepth() != null ? vo.getDepth() : 1)
                .code(vo.getCode())
                .codeName(vo.getCodeName())
                .codeValue(vo.getCodeValue())
                .sortOrder(vo.getSortOrder() != null ? vo.getSortOrder() : 0)
                .isActive(vo.getIsActive() == null || Boolean.TRUE.equals(vo.getIsActive()))
                .isSystem(Boolean.TRUE.equals(vo.getIsSystem()))
                .startAt(vo.getStartAt())
                .endAt(vo.getEndAt())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .childCount(vo.getChildCount())
                .children(childrenList)
                .build();
    }
}

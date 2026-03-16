package com.nt.cms.role.dto;

import com.nt.cms.role.vo.PermissionVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 권한 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {

    private Long id;
    private String permissionCode;
    private String permissionName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static PermissionResponse from(PermissionVO vo) {
        if (vo == null) {
            return null;
        }
        return PermissionResponse.builder()
                .id(vo.getId())
                .permissionCode(vo.getPermissionCode())
                .permissionName(vo.getPermissionName())
                .description(vo.getDescription())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}

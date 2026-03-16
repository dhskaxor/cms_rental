package com.nt.cms.role.dto;

import com.nt.cms.role.vo.PermissionVO;
import com.nt.cms.role.vo.RoleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 역할 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private List<PermissionResponse> permissions;
    private Long userCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static RoleResponse from(RoleVO vo) {
        if (vo == null) {
            return null;
        }

        List<PermissionResponse> permissionResponses = null;
        if (vo.getPermissions() != null) {
            permissionResponses = vo.getPermissions().stream()
                    .map(PermissionResponse::from)
                    .collect(Collectors.toList());
        }

        return RoleResponse.builder()
                .id(vo.getId())
                .roleCode(vo.getRoleCode())
                .roleName(vo.getRoleName())
                .description(vo.getDescription())
                .permissions(permissionResponses)
                .userCount(vo.getUserCount())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }

    /**
     * 권한 응답 DTO (내부 클래스)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionResponse {
        private Long id;
        private String permissionCode;
        private String permissionName;
        private String description;

        public static PermissionResponse from(PermissionVO vo) {
            if (vo == null) {
                return null;
            }
            return PermissionResponse.builder()
                    .id(vo.getId())
                    .permissionCode(vo.getPermissionCode())
                    .permissionName(vo.getPermissionName())
                    .description(vo.getDescription())
                    .build();
        }
    }
}

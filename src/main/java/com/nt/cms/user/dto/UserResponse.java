package com.nt.cms.user.dto;

import com.nt.cms.user.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static UserResponse from(UserVO vo) {
        if (vo == null) {
            return null;
        }
        return UserResponse.builder()
                .id(vo.getId())
                .username(vo.getUsername())
                .name(vo.getName())
                .email(vo.getEmail())
                .roleId(vo.getRoleId())
                .roleCode(vo.getRoleCode())
                .roleName(vo.getRoleName())
                .status(vo.getStatus())
                .lastLoginAt(vo.getLastLoginAt())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}

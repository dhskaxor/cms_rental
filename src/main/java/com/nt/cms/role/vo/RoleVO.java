package com.nt.cms.role.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 역할 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO extends BaseVO {

    /**
     * 역할 코드 (ADMIN, MANAGER, USER 등)
     */
    private String roleCode;

    /**
     * 역할명
     */
    private String roleName;

    /**
     * 역할 설명
     */
    private String description;

    /**
     * 권한 목록 (조인)
     */
    private List<PermissionVO> permissions;

    /**
     * 사용자 수 (집계)
     */
    private Long userCount;
}

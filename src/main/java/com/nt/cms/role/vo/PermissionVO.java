package com.nt.cms.role.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 권한 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO extends BaseVO {

    /**
     * 권한 코드 (USER_CREATE, USER_READ 등)
     */
    private String permissionCode;

    /**
     * 권한명
     */
    private String permissionName;

    /**
     * 권한 설명
     */
    private String description;
}

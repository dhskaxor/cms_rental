package com.nt.cms.commoncode.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 공통 코드 그룹 VO
 *
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupVO extends BaseVO {

    /**
     * 코드 그룹 고유 코드
     */
    private String groupCode;

    /**
     * 코드 그룹명
     */
    private String groupName;

    /**
     * 설명
     */
    private String description;

    /**
     * 시스템 코드 여부 (수정 제한)
     */
    private Boolean isSystem;

    /**
     * 활성 여부
     */
    private Boolean isActive;

    /**
     * 그룹 내 코드 수 (집계)
     */
    private Long codeCount;
}

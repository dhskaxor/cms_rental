package com.nt.cms.commoncode.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 공통 코드 VO
 *
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeVO extends BaseVO {

    /**
     * 코드 그룹 ID
     */
    private Long groupId;

    /**
     * 상위 코드 ID (계층 구조)
     */
    private Long parentId;

    /**
     * 코드 깊이
     */
    private Integer depth;

    /**
     * 코드 값 (그룹 내 유일)
     */
    private String code;

    /**
     * 코드명
     */
    private String codeName;

    /**
     * 실제 저장 값 (선택)
     */
    private String codeValue;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /**
     * 사용 여부
     */
    private Boolean isActive;

    /**
     * 시스템 코드 여부
     */
    private Boolean isSystem;

    /**
     * 사용 시작일
     */
    private LocalDateTime startAt;

    /**
     * 사용 종료일
     */
    private LocalDateTime endAt;

    /**
     * 그룹 코드 (조인)
     */
    private String groupCode;

    /**
     * 하위 코드 목록 (계층)
     */
    private List<CommonCodeVO> children;

    /**
     * 하위 코드 수 (집계)
     */
    private Integer childCount;
}

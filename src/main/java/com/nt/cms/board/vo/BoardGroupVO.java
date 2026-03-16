package com.nt.cms.board.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 게시판 그룹 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGroupVO extends BaseVO {

    /**
     * 그룹 코드
     */
    private String groupCode;

    /**
     * 그룹명
     */
    private String groupName;

    /**
     * 게시판 수 (집계)
     */
    private Integer boardCount;
}

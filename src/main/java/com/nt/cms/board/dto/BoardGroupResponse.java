package com.nt.cms.board.dto;

import com.nt.cms.board.vo.BoardGroupVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시판 그룹 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGroupResponse {

    private Long id;
    private String groupCode;
    private String groupName;
    private Integer boardCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static BoardGroupResponse from(BoardGroupVO vo) {
        if (vo == null) {
            return null;
        }
        return BoardGroupResponse.builder()
                .id(vo.getId())
                .groupCode(vo.getGroupCode())
                .groupName(vo.getGroupName())
                .boardCount(vo.getBoardCount())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}

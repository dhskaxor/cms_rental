package com.nt.cms.board.dto;

import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {

    private Long id;
    private String boardCode;
    private String boardName;
    private Long groupId;
    private String groupName;
    private Boolean useComment;
    private Boolean useFile;
    private Integer maxFileCount;
    private Boolean useSecret;
    private Integer pageSize;
    private String templateCode;
    private Boolean useEditor;
    private Long postCount;
    private List<BoardPermissionResponse> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static BoardResponse from(BoardVO vo) {
        if (vo == null) {
            return null;
        }

        List<BoardPermissionResponse> permissionResponses = null;
        if (vo.getPermissions() != null) {
            permissionResponses = vo.getPermissions().stream()
                    .map(BoardPermissionResponse::from)
                    .collect(Collectors.toList());
        }

        return BoardResponse.builder()
                .id(vo.getId())
                .boardCode(vo.getBoardCode())
                .boardName(vo.getBoardName())
                .groupId(vo.getGroupId())
                .groupName(vo.getGroupName())
                .useComment(vo.getUseComment())
                .useFile(vo.getUseFile())
                .maxFileCount(vo.getMaxFileCount())
                .useSecret(vo.getUseSecret())
                .pageSize(vo.getPageSize())
                .templateCode(vo.getTemplateCode())
                .useEditor(vo.getUseEditor())
                .postCount(vo.getPostCount())
                .permissions(permissionResponses)
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }

    /**
     * 게시판 권한 응답 DTO (내부 클래스)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardPermissionResponse {
        private Long id;
        private Long roleId;
        private String roleCode;
        private String roleName;
        private Boolean canCreate;
        private Boolean canRead;
        private Boolean canUpdate;
        private Boolean canDelete;

        public static BoardPermissionResponse from(BoardPermissionVO vo) {
            if (vo == null) {
                return null;
            }
            return BoardPermissionResponse.builder()
                    .id(vo.getId())
                    .roleId(vo.getRoleId())
                    .roleCode(vo.getRoleCode())
                    .roleName(vo.getRoleName())
                    .canCreate(vo.getCanCreate())
                    .canRead(vo.getCanRead())
                    .canUpdate(vo.getCanUpdate())
                    .canDelete(vo.getCanDelete())
                    .build();
        }
    }
}

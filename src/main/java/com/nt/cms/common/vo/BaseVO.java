package com.nt.cms.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 모든 VO의 기본 클래스
 * 
 * <p>공통 필드를 정의하며, 모든 VO는 이 클래스를 상속받는다.</p>
 * <ul>
 *   <li>id: Primary Key</li>
 *   <li>createdAt: 생성 일시</li>
 *   <li>createdBy: 생성자 ID</li>
 *   <li>updatedAt: 수정 일시</li>
 *   <li>updatedBy: 수정자 ID</li>
 *   <li>deleted: Soft Delete 플래그</li>
 * </ul>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseVO {

    /**
     * Primary Key
     */
    private Long id;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 생성자 ID
     */
    private Long createdBy;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    /**
     * 수정자 ID
     */
    private Long updatedBy;

    /**
     * Soft Delete 플래그 (false: 활성, true: 삭제)
     */
    @lombok.Builder.Default
    private Boolean deleted = false;

    /**
     * 엔티티 생성 시 호출
     * 생성 일시를 현재 시간으로 설정
     * 
     * @param userId 생성자 ID
     */
    public void onCreate(Long userId) {
        this.createdAt = LocalDateTime.now();
        this.createdBy = userId;
        this.deleted = false;
    }

    /**
     * 엔티티 수정 시 호출
     * 수정 일시를 현재 시간으로 설정
     * 
     * @param userId 수정자 ID
     */
    public void onUpdate(Long userId) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    /**
     * Soft Delete 수행
     * 
     * @param userId 삭제자 ID
     */
    public void onDelete(Long userId) {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    /**
     * 삭제 여부 확인
     * 
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }
}

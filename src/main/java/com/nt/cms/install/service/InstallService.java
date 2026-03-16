package com.nt.cms.install.service;

import com.nt.cms.install.dto.AdminAccountRequest;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.dto.SiteConfigRequest;

/**
 * 설치 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface InstallService {

    /**
     * 설치 완료 여부 확인
     * 
     * @return 설치 완료되었으면 true
     */
    boolean isInstalled();

    /**
     * 데이터베이스 연결 테스트
     * 
     * @param request DB 연결 정보
     * @return 연결 성공하면 true
     */
    boolean testDatabaseConnection(DatabaseConfigRequest request);

    /**
     * 데이터베이스 설정 저장 (세션에 임시 저장)
     * 
     * @param request DB 연결 정보
     */
    void saveDatabaseConfig(DatabaseConfigRequest request);

    /**
     * 저장된 데이터베이스 설정 조회
     * 
     * @return DB 연결 정보
     */
    DatabaseConfigRequest getDatabaseConfig();

    /**
     * 스키마 실행 (테이블 생성)
     *
     * @return 성공하면 true
     */
    boolean executeSchema();

    /**
     * 지정된 스키마 파일로 스키마 실행 (테스트용)
     *
     * @param schemaPath classpath 기준 스키마 파일 경로
     * @return 성공하면 true
     */
    boolean executeSchema(String schemaPath);

    /**
     * 초기 데이터 삽입
     * 
     * @return 성공하면 true
     */
    boolean insertInitialData();

    /**
     * 관리자 계정 생성
     * 
     * @param request 관리자 계정 정보
     * @return 성공하면 true
     */
    boolean createAdminAccount(AdminAccountRequest request);

    /**
     * 사이트 설정 저장
     * 
     * @param request 사이트 설정 정보
     */
    void saveSiteConfig(SiteConfigRequest request);

    /**
     * 설치 완료 처리
     * cms-config.yml 파일 생성 및 installed 플래그 설정
     * 
     * @return 성공하면 true
     */
    boolean completeInstallation();

    /**
     * 설치 상태 초기화 (재설치용)
     */
    void resetInstallation();
}

package com.nt.cms.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * CMS 설정 프로퍼티
 * 
 * <p>application.yml의 cms.* 설정을 바인딩하는 클래스</p>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cms")
public class CmsProperties {

    /**
     * 설치 완료 여부
     */
    private boolean installed = false;

    /**
     * 설치 시 저장한 데이터베이스 설정 (cms-config.yml)
     */
    private Database database = new Database();

    /**
     * JWT 설정
     */
    private Jwt jwt = new Jwt();

    /**
     * 파일 업로드 설정
     */
    private File file = new File();

    /**
     * 보안 설정
     */
    private Security security = new Security();

    /**
     * JWT 설정 클래스
     */
    @Getter
    @Setter
    public static class Jwt {
        /**
         * JWT 비밀키
         */
        private String secret = "cms-default-secret-key";
        
        /**
         * Access Token 유효 시간 (초)
         */
        private long accessTokenValidity = 3600; // 1시간
        
        /**
         * Refresh Token 유효 시간 (초)
         */
        private long refreshTokenValidity = 1209600; // 14일
    }

    /**
     * 파일 업로드 설정 클래스
     */
    @Getter
    @Setter
    public static class File {
        /**
         * 업로드 경로
         */
        private String uploadPath = "C:/cms/files";
        
        /**
         * 최대 파일 크기 (바이트)
         */
        private long maxSize = 3145728; // 3MB
        
        /**
         * 허용 확장자
         */
        private Map<String, String> allowedExtensions = new HashMap<>();
        
        /**
         * 이미지 허용 확장자 반환
         * 
         * @return 허용 확장자 배열
         */
        public String[] getAllowedImageExtensions() {
            String imageExtensions = allowedExtensions.getOrDefault("image", "jpg,jpeg,png,gif,webp");
            return imageExtensions.split(",");
        }
        
        /**
         * 문서 허용 확장자 반환
         * 
         * @return 허용 확장자 배열
         */
        public String[] getAllowedDocumentExtensions() {
            String documentExtensions = allowedExtensions.getOrDefault("document", "pdf,doc,docx,xls,xlsx,hwp");
            return documentExtensions.split(",");
        }
        
        /**
         * 모든 허용 확장자 반환
         * 
         * @return 허용 확장자 배열
         */
        public String[] getAllAllowedExtensions() {
            String[] imageExtensions = getAllowedImageExtensions();
            String[] documentExtensions = getAllowedDocumentExtensions();
            
            String[] allExtensions = new String[imageExtensions.length + documentExtensions.length];
            System.arraycopy(imageExtensions, 0, allExtensions, 0, imageExtensions.length);
            System.arraycopy(documentExtensions, 0, allExtensions, imageExtensions.length, documentExtensions.length);
            
            return allExtensions;
        }
    }

    /**
     * 설치 시 설정한 데이터베이스 정보
     */
    @Getter
    @Setter
    public static class Database {
        private String host = "localhost";
        private int port = 3306;
        private String name = "cms";
        private String username = "root";
        private String password = "";
    }

    /**
     * 보안 설정 클래스
     */
    @Getter
    @Setter
    public static class Security {
        /**
         * 최대 로그인 실패 횟수
         */
        private int maxLoginAttempts = 5;
        
        /**
         * 최소 비밀번호 길이
         */
        private int passwordMinLength = 6;
        
        /**
         * 최대 비밀번호 길이
         */
        private int passwordMaxLength = 12;
    }
}

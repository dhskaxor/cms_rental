package com.nt.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * CMS Core Platform 메인 애플리케이션
 * 
 * <p>재사용 가능한 CMS Core 시스템의 진입점</p>
 * <p>Spring Boot 3.2.x + MyBatis 기반</p>
 * 
 * @author CMS Team
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootApplication
@EnableCaching
public class CmsApplication {

    /**
     * 애플리케이션 시작점
     * 
     * @param args 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}

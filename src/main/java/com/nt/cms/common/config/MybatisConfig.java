package com.nt.cms.common.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis 설정
 * 
 * <p>DatabaseIdProvider: H2 등 DB별 SQL 분기 (user 예약어 대응)</p>
 *
 * @author CMS Team
 */
@Configuration
public class MybatisConfig {

    /**
     * DB 벤더별 databaseId 설정
     * H2에서 user 테이블은 예약어이므로 "user"로 쿼리해야 함
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        Properties props = new Properties();
        props.setProperty("H2", "h2");
        props.setProperty("MariaDB", "mariadb");
        props.setProperty("MySQL", "mariadb");  // MySQL도 user 예약어 동일
        props.setProperty("PostgreSQL", "postgresql");
        provider.setProperties(props);
        return provider;
    }
}

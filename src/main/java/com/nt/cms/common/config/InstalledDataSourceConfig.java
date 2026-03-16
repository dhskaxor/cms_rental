package com.nt.cms.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 설치 완료 후 cms-config.yml의 DB를 사용하는 DataSource 설정
 *
 * <p>cms.installed=true이고 cms.database가 설정된 경우,
 * application-dev의 H2 대신 설치 시 지정한 MariaDB를 사용한다.</p>
 *
 * @author CMS Team
 */
@Configuration
@ConditionalOnProperty(name = "cms.installed", havingValue = "true")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class InstalledDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(CmsProperties cmsProperties) {
        CmsProperties.Database db = cmsProperties.getDatabase();
        String jdbcUrl = String.format(
                "jdbc:mariadb://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul",
                db.getHost(), db.getPort(), db.getName());

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(db.getUsername());
        ds.setPassword(db.getPassword() != null ? db.getPassword() : "");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        return ds;
    }
}

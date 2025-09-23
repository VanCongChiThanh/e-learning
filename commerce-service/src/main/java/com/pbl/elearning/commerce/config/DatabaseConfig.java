package com.pbl.elearning.commerce.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Database Configuration for Commerce Service
 * Cấu hình Database cho Commerce Service
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:60000}")
    private long maxLifetime;

    /**
     * Cấu hình HikariCP DataSource cho hiệu suất tối ưu
     */
    @Bean
    @Profile("!test")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Basic connection settings
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setMaxLifetime(maxLifetime);

        // Performance settings
        config.setAutoCommit(false);
        config.setLeakDetectionThreshold(60000);

        // Connection test settings
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // Pool name for monitoring
        config.setPoolName("CommerceServicePool");

        return new HikariDataSource(config);
    }
}

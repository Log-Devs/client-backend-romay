package com.logistics.LogisticsFuture.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {
    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() throws Exception {
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set");
        }

        // Parse the Render DATABASE_URL
        URI dbUri = new URI(databaseUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        // Use 5432 as the default port if none is specified
        String port = dbUri.getPort() == -1 ? "5432" : String.valueOf(dbUri.getPort());
        String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbUri.getPath() + "?sslmode=require";

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }
}

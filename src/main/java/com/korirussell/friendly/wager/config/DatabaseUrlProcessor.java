package com.korirussell.friendly.wager.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts DATABASE_URL style values (e.g. postgres://user:pass@host:5432/db?sslmode=require)
 * into Spring Boot friendly properties: spring.datasource.url/username/password.
 *
 * This helps platforms like Render/Heroku when only DATABASE_URL is provided.
 */
public class DatabaseUrlProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // If standard properties already set, don't override
        String existingUrl = environment.getProperty("spring.datasource.url");
        if (existingUrl != null && !existingUrl.isBlank()) {
            return;
        }

        // Prefer explicit DB_URL/JDBC_DATABASE_URL; else try DATABASE_URL
        String dbUrl = environment.getProperty("DB_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = environment.getProperty("JDBC_DATABASE_URL");
        }
        String databaseUrl = environment.getProperty("DATABASE_URL");

        Map<String, Object> overrides = new HashMap<>();

        if (dbUrl != null && !dbUrl.isBlank()) {
            // Assume already JDBC; only set url if present
            overrides.put("spring.datasource.url", dbUrl);
        } else if (databaseUrl != null && !databaseUrl.isBlank()) {
            try {
                ParsedDb p = parseDatabaseUrl(databaseUrl);
                overrides.put("spring.datasource.url", p.jdbcUrl);
                if (p.username != null) overrides.put("spring.datasource.username", p.username);
                if (p.password != null) overrides.put("spring.datasource.password", p.password);
            } catch (URISyntaxException ignored) {
                // leave to default resolution
            }
        }

        if (!overrides.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("db-url-processor", overrides));
        }
    }

    private record ParsedDb(String jdbcUrl, String username, String password) {}

    private static ParsedDb parseDatabaseUrl(String raw) throws URISyntaxException {
        // Accept postgres:// or postgresql://
        String normalized = raw;
        if (normalized.startsWith("postgres://")) {
            normalized = normalized.replaceFirst("^postgres://", "postgresql://");
        }
        URI uri = new URI(normalized);

        String userInfo = uri.getUserInfo();
        String username = null;
        String password = null;
        if (userInfo != null) {
            int idx = userInfo.indexOf(':');
            if (idx >= 0) {
                username = percentDecode(userInfo.substring(0, idx));
                password = percentDecode(userInfo.substring(idx + 1));
            } else {
                username = percentDecode(userInfo);
            }
        }

        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath(); // leading /db
        String db = (path != null && path.startsWith("/")) ? path.substring(1) : path;
        String query = uri.getQuery();

        StringBuilder jdbc = new StringBuilder("jdbc:postgresql://");
        jdbc.append(host);
        if (port > 0) jdbc.append(":").append(port);
        jdbc.append("/").append(db == null ? "" : db);
        // Ensure sslmode=require present unless already supplied
        if (query == null || query.isBlank()) {
            jdbc.append("?sslmode=require");
        } else {
            if (!query.contains("sslmode=")) {
                jdbc.append("?").append(query).append("&sslmode=require");
            } else {
                jdbc.append("?").append(query);
            }
        }

        return new ParsedDb(jdbc.toString(), username, password);
    }

    private static String percentDecode(String s) {
        return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        // Run early so Spring's DataSource auto-config sees our properties
        return Ordered.HIGHEST_PRECEDENCE;
    }
}


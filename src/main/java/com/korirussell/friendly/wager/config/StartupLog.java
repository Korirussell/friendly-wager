package com.korirussell.friendly.wager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupLog implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupLog.class);
    private final Environment env;

    public StartupLog(Environment env) { this.env = env; }

    @Override
    public void run(ApplicationArguments args) {
        String profile = String.join(",", env.getActiveProfiles());
        String url = env.getProperty("spring.datasource.url", "<none>");
        String user = env.getProperty("spring.datasource.username", "<none>");
        String port = env.getProperty("server.port", "<default>");
        log.info("Active profile(s): {}", profile);
        log.info("Resolved datasource url: {}", url);
        log.info("Resolved datasource username present: {}", !"<none>".equals(user));
        log.info("Server port: {}", port);
    }
}


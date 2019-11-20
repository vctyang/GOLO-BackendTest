package io.golo.backendtest;

import io.golo.backendtest.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot application
 */
@SpringBootApplication
@Import(SwaggerConfig.class)
public class BackendTestApplication {

    /**
     * Spring Boot application runner
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendTestApplication.class, args);
    }

}

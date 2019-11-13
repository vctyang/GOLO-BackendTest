package io.golo.backendtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration to enable Swagger
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Bean to return Swagger API
     *
     * @return A Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.golo.backendtest"))
                .paths(PathSelectors.any())
                .build();
    }

}

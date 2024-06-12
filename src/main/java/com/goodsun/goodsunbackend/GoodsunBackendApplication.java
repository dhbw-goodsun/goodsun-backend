package com.goodsun.goodsunbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main class of the application, which starts the Spring Boot container.
 *
 * @author Jonas Nunnenmacher
 */
@SpringBootApplication
public class GoodsunBackendApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(GoodsunBackendApplication.class, args);
    }

    /**
     * Configuration method for CORS support.
     *
     * @return A {@code WebMvcConfigurer} bean for CORS configuration.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://goodsun-frontend.onrender.com")
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
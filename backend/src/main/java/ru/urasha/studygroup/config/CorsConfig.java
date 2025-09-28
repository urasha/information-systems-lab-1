package ru.urasha.studygroup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5174/~s413022",
                        "http://localhost:5173/~s413022",
                        "http://localhost:3000",
                        "https://se.ifmo.ru",
                        "https://helios.cs.ifmo.ru",
                        "http://helios.cs.ifmo.ru"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

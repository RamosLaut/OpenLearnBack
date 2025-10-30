package com.openlearn.OpenLearn.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.serve-path}")
    private String servePath; // e.g., /uploads/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure servePath ends with '/' if needed by the pattern
        String servePattern = servePath + "**"; // e.g., /uploads/**
        // Ensure uploadDir has 'file:' prefix and ends with '/'
        String location = "file:" + Paths.get(uploadDir).toAbsolutePath().normalize().toString() + "/";

        System.out.println("Mapping URL pattern '" + servePattern + "' to location '" + location + "'");

        registry.addResourceHandler(servePattern) // The URL path pattern
                .addResourceLocations(location);   // The actual directory on disk
    }
}

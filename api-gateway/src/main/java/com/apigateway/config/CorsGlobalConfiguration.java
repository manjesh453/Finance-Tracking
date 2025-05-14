//package com.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsGlobalConfiguration {
//
//    private static final String GET = "GET";
//    private static final String POST = "POST";
//    private static final String DELETE = "DELETE";
//    private static final String PUT = "PUT";
//    private static final String OPTION = "OPTION";
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                WebMvcConfigurer.super.addCorsMappings(registry);
//                registry.addMapping("/**")
//                        .allowedMethods(GET, POST, PUT, DELETE,OPTION)
//                        .allowedOrigins("http://localhost:3000")
//                        .allowedHeaders("*")
//                        .allowedOriginPatterns("*")
//                        .allowCredentials(true)
//                ;
//            }
//        };
//    }
//}

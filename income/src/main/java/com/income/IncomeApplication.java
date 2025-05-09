package com.income;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@SpringBootApplication
@EnableDiscoveryClient
public class IncomeApplication extends SpringBootServletInitializer {


    static Properties getProperties() {
        Properties props = new Properties();
        props.put("spring.config.location", "file:${catalina.home}/conf/micro-income.yml");
        return props;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(IncomeApplication.class)
                .sources(IncomeApplication.class)
                .properties(getProperties())
                .run(args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

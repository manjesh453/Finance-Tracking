package com.users.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
//    @LoadBalanced    Enable it for loadbalance instead of direct hit to localhost
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

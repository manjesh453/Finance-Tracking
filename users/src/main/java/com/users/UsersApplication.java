package com.users;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Properties;

@SpringBootApplication
@EnableDiscoveryClient
public class UsersApplication extends SpringBootServletInitializer {

    static Properties getProperties() {
        Properties props = new Properties();
        props.put("spring.config.location", "file:${catalina.home}/conf/micro_user.yml");
        return props;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(UsersApplication.class)
                .sources(UsersApplication.class)
                .properties(getProperties())
                .run(args);
    }
}

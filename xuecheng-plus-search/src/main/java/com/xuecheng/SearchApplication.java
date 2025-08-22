package com.xuecheng;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.core.LoggerContext;

@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        //LoggerContext context = (LoggerContext) LogManager.getContext(false);
        //System.out.println("Log4j2 Configuration File: " + context.getConfigLocation());

        SpringApplication.run(SearchApplication.class, args);
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        System.out.println("Log4j2 Configuration File: " + context.getConfigLocation());
    }

}

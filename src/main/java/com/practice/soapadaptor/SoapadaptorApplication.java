package com.practice.soapadaptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SoapadaptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapadaptorApplication.class, args);

    }

}

package com.myProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
public class ECommerceBackendProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ECommerceBackendProjectApplication.class, args);
    }
}

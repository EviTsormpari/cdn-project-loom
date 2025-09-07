package com.example.CdnEdgeServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CdnEdgeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdnEdgeServerApplication.class, args);
	}

}
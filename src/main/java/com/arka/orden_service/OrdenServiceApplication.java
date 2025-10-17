package com.arka.orden_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrdenServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdenServiceApplication.class, args);
	}

}

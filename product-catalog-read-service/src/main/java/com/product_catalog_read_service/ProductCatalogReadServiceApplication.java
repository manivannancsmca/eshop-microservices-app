package com.product_catalog_read_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProductCatalogReadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCatalogReadServiceApplication.class, args);
	}

}

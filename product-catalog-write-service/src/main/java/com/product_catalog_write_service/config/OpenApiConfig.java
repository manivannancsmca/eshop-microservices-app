package com.product_catalog_write_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog Service API")
                        .version("1.0.0")
                        .description("Production-ready REST API for managing a product catalog")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("engineering@productcatalog.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8101").description("Local development")
                        //new Server().url("http://localhost:8090").description("Docker")
                    ));
    }
}

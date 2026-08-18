package com.danieloliveira.order_management.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gerenciamento de Encomendas")
                        .description("API para auxiliar pequenos produtores a organizar pedidos, " +
                                "controlar a produção diária e receber alertas de prazo, substituindo o controle manual em caderno.")
                        .version("v1"));
    }
}

package br.com.caiquedev.pdvloja.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customAPI(){
        return new OpenAPI().info(new Info()
                .title("Ponto de Vendas (PDV) API")
                .version("1.0.0")
                .description("API REST para gerenciamento de um Ponto de Vendas (PDV) de loja de roupas. " +
                "Permite cadastrar clientes, categorias, produtos, controlar estoque e registrar vendas.")
                .termsOfService("https://swagger.io/terms/")
                .license(new License().name("MIT").url("https://mit-license.org/"))
                .contact(new Contact().name("José Caique")
                .url("https://github.com/caiquedevbr")
                .email("jcaique825@gmail.com"))

        );
    }
}

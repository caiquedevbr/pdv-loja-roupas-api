package br.com.caiquedev.pdvloja.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Versionamento da API da PDV Loja pelo header X-API-Version.
 *
 * A ausencia do header seleciona a V1 para preservar os clientes atuais.
 * Endpoints de versoes posteriores devem declarar o atributo version nas
 * anotacoes de mapeamento, por exemplo: @GetMapping(version = "2").
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    public static final String HEADER = "X-API-Version";

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader(HEADER)
                .setDefaultVersion("1");
    }
}

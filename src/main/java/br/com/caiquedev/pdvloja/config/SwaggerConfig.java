package br.com.caiquedev.pdvloja.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SwaggerConfig {

    private static final String TERMOS_DE_USO_V1 = "/api/termos-de-uso";
    private static final String TERMOS_DE_USO_V2 = "/api/v2/termos-de-uso";

    @Bean
    public OpenAPI customAPI() {
        return new OpenAPI().info(new Info()
                .title("Ponto de Vendas (PDV) API")
                .version("2.0.0")
                .termsOfService(TERMOS_DE_USO_V1)
                .description(documentacaoGeral())
                .license(new License().name("MIT").url("https://mit-license.org/"))
                .contact(new Contact().name("José Caique")
                        .url("https://github.com/caiquedevbr")
                        .email("jcaique825@gmail.com")));
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("PDV Loja API V1 - compatível")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/v1/**", "/api/v2/**")
                .addOpenApiCustomizer(openApi -> {
                    aplicarErros(openApi);
                    openApi.getInfo().setDescription(documentacaoV1());
                    openApi.getInfo().setTermsOfService(TERMOS_DE_USO_V1);
                })
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("PDV Loja API V2 - recursos avançados")
                .pathsToMatch("/api/v2/**")
                .addOpenApiCustomizer(this::configurarV2)
                .build();
    }

    private void configurarV2(OpenAPI openApi) {
        aplicarErros(openApi);
        openApi.getInfo().setDescription(documentacaoV2());
        openApi.getInfo().setTermsOfService(TERMOS_DE_USO_V2);
        openApi.getComponents().addSecuritySchemes("apiKeyAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY)
                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                .name("X-API-Key")
                .description("Informe em X-API-Key somente o valor de `apiKey` retornado pelo login. "
                        + "A chave é mostrada uma única vez."));

        if (openApi.getPaths() == null) {
            return;
        }

        openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap()
                .forEach((method, operation) -> {
                    if ("/api/v2/termos-de-uso".equals(path)) {
                        return;
                    }
                    operation.addParametersItem(cabecalhoVersao());
                    if ("/api/v2/vendas".equals(path)
                            && method == io.swagger.v3.oas.models.PathItem.HttpMethod.POST) {
                        operation.addParametersItem(cabecalhoIdempotencia());
                    }
                    if (!"/api/v2/auth/login".equals(path)) {
                        operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                                .addList("apiKeyAuth"));
                    }
                    operation.getResponses().addApiResponse("401", respostaErro(
                            "API Key ausente, inválida ou revogada."));
                    operation.getResponses().addApiResponse("429", respostaRateLimit());
                }));
    }

    private void aplicarErros(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }
        components.addSchemas("ApiErrorResponse", erroSchema());

        if (openApi.getPaths() == null) {
            return;
        }
        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation ->
                operation.getResponses().forEach((status, response) -> {
                    if (Set.of("400", "401", "404", "409", "429", "500").contains(status)) {
                        response.setContent(erroContent());
                    }
                })));
    }

    private HeaderParameter cabecalhoVersao() {
        HeaderParameter parametro = new HeaderParameter();
        parametro.setName("X-API-Version");
        parametro.setRequired(true);
        parametro.setDescription("Obrigatório na V2; use exatamente o valor 2.");
        StringSchema schema = new StringSchema();
        schema.addEnumItemObject("2");
        parametro.setSchema(schema);
        return parametro;
    }

    private HeaderParameter cabecalhoIdempotencia() {
        HeaderParameter parametro = new HeaderParameter();
        parametro.setName("X-Idempotency-Key");
        parametro.setRequired(true);
        parametro.setDescription("Chave única da tentativa de venda. Reenvios iguais devolvem a resposta original.");
        StringSchema schema = new StringSchema();
        schema.setExample("1d8821c9-9b52-4c58-b714-bfa0c7a71dbd");
        parametro.setSchema(schema);
        return parametro;
    }

    private ApiResponse respostaErro(String descricao) {
        return new ApiResponse().description(descricao).content(erroContent());
    }

    private ApiResponse respostaRateLimit() {
        return respostaErro("Limite de requisições excedido. Consulte Retry-After antes de tentar novamente.")
                .addHeaderObject("X-RateLimit-Limit", new io.swagger.v3.oas.models.headers.Header()
                        .description("Limite configurado por minuto.").schema(new IntegerSchema().example(60)))
                .addHeaderObject("X-RateLimit-Remaining", new io.swagger.v3.oas.models.headers.Header()
                        .description("Requisições restantes na janela atual.").schema(new IntegerSchema().example(0)))
                .addHeaderObject("X-RateLimit-Reset", new io.swagger.v3.oas.models.headers.Header()
                        .description("Unix timestamp do início da próxima janela.").schema(new IntegerSchema().format("int64")))
                .addHeaderObject("Retry-After", new io.swagger.v3.oas.models.headers.Header()
                        .description("Segundos até uma nova tentativa.").schema(new IntegerSchema().example(30)));
    }

    private Schema<?> erroSchema() {
        ObjectSchema detalhes = new ObjectSchema();
        detalhes.additionalProperties(new StringSchema());
        return new ObjectSchema()
                .description("Resposta de erro padronizada pelo ApiExceptionHandler")
                .addProperty("type", new StringSchema().example("https://api.pdv-loja.local/errors/400"))
                .addProperty("title", new StringSchema().example("Dados inválidos"))
                .addProperty("status", new IntegerSchema().example(400))
                .addProperty("detail", new StringSchema().example("Corrija os campos informados e envie a requisição novamente."))
                .addProperty("mensagem", new StringSchema().example("Corrija os campos informados e envie a requisição novamente."))
                .addProperty("timestamp", new StringSchema().format("date-time"))
                .addProperty("detalhes", detalhes);
    }

    private Content erroContent() {
        return new Content().addMediaType("application/problem+json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse")));
    }

    private String documentacaoGeral() {
        return """
                ## Apresentação
                API REST de um ponto de vendas de roupas. Ela gerencia clientes, categorias, produtos, estoque e vendas,
                com Java 17, Spring Boot, Spring Data JPA, H2, Spring HATEOAS e Springdoc OpenAPI.

                ## Comece aqui
                Execute `./mvnw spring-boot:run` e abra `/swagger-ui/index.html`. Cadastre categoria, cliente, produto e
                estoque antes de registrar uma venda. Listagens aceitam `page`, `size` (máximo 100) e `sort=campo,asc`.
                Respostas de recursos trazem links HATEOAS em `_links`; erros usam `application/problem+json`.

                ## Versões
                A V1 preserva os contratos existentes em `/api/**`.
                A V2 usa `/api/v2/**`, requer `X-API-Version: 2` e acrescenta API Key, rate limiting e idempotência
                para criação de vendas. Um header de versão inválido na V2 retorna 400.

                ## Códigos principais
                `200` consulta/alteração, `201` criação, `204` exclusão, `400` dados ou versão inválidos, `401` API Key
                ausente/inválida, `404` não encontrado, `409` conflito de regra ou idempotência e `429` limite excedido.
                """;
    }

    private String documentacaoV1() {
        return documentacaoGeral() + "\n\n## Uso da V1\nA V1 não exige `X-API-Version` nem `X-API-Key`. "
                + "Use-a para consumidores legados; os recursos avançados estão exclusivamente na V2.";
    }

    private String documentacaoV2() {
        return documentacaoGeral() + """

                ## Autenticação por API Key
                Primeiro envie `POST /api/v2/auth/login` com `X-API-Version: 2`, e-mail e senha válidos. A resposta contém
                `apiKey` apenas nessa emissão. Clique em **Authorize** e informe essa chave no campo `X-API-Key` para os
                demais endpoints V2. A chave expira 24 horas após a emissão. `POST /api/v2/auth/logout` revoga a chave atual.

                ## Idempotência de vendas
                Em `POST /api/v2/vendas`, informe também `X-Idempotency-Key` (por exemplo, um UUID). A mesma chave com o
                mesmo corpo retorna `201` e o corpo original, com `Idempotent-Replayed: true`, sem nova baixa de estoque.
                A mesma chave com corpo diferente retorna `409`. Os registros expiram após 24 horas.

                ## Rate limiting
                O padrão é 60 requisições por minuto por usuário autenticado; o login usa o IP. Verifique
                `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` e `Retry-After`. Ao exceder a janela,
                a API retorna `429`. A estratégia persiste os contadores no banco; em múltiplas instâncias, use um banco
                compartilhado e coordenação distribuída adicional para obter uma janela global estrita.

                ## Ordem recomendada de cadastro
                Crie uma categoria, depois um cliente, produto e estoque. Por fim, registre a venda em `/api/v2/vendas`.
                """;
    }
}

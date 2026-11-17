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
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Bean
    public GroupedOpenApi v1Api() {
        return versionGroup("1", true);
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return versionGroup("2", false);
    }

    private static GroupedOpenApi versionGroup(String version, boolean incluirEndpointsSemVersao) {
        return GroupedOpenApi.builder()
                .group("v" + version)
                .displayName("PDV Loja API v" + version)
                .addOpenApiMethodFilter(method -> pertenceAVersao(method, version, incluirEndpointsSemVersao))
                .addOperationCustomizer((operation, handlerMethod) -> {
                    if ("2".equals(version)) {
                        documentarHeaderDaVersao(operation, version);
                    }
                    return operation;
                })
                .build();
    }

    private static boolean pertenceAVersao(Method method, String version, boolean incluirEndpointsSemVersao) {
        String versaoDoEndpoint = versaoDoEndpoint(method);
        return versaoDoEndpoint.equals(version) || (incluirEndpointsSemVersao && versaoDoEndpoint.isEmpty());
    }

    private static String versaoDoEndpoint(Method method) {
        RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (methodMapping != null && !methodMapping.version().isBlank()) {
            return methodMapping.version();
        }

        RequestMapping controllerMapping = AnnotatedElementUtils.findMergedAnnotation(
                method.getDeclaringClass(), RequestMapping.class);
        return controllerMapping == null ? "" : controllerMapping.version();
    }

    private static void documentarHeaderDaVersao(io.swagger.v3.oas.models.Operation operation, String version) {
        List<Parameter> parameters = operation.getParameters() == null
                ? new ArrayList<>()
                : operation.getParameters();

        boolean headerJaDocumentado = parameters.stream()
                .anyMatch(parameter -> ApiVersionConfig.HEADER.equals(parameter.getName())
                        && "header".equals(parameter.getIn()));

        if (!headerJaDocumentado) {
            parameters.add(new Parameter()
                    .name(ApiVersionConfig.HEADER)
                    .in("header")
                    .required(true)
                    .description("Seleciona a versao 2 da API")
                    .schema(new StringSchema()._default(version)._enum(List.of(version))));
            operation.setParameters(parameters);
        }
    }

    @Bean
    public OpenApiCustomizer errorResponseCustomizer() {
        return openApi -> {
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
                        if (Set.of("400", "404", "409").contains(status)) {
                            response.setContent(erroContent());
                        }
                    })
            ));
        };
    }

    private Schema<?> erroSchema() {
        ObjectSchema detalhes = new ObjectSchema();
        detalhes.additionalProperties(new StringSchema());

        return new ObjectSchema()
                .description("Resposta de erro retornada pelo ApiExceptionHandler")
                .addProperty("type", new StringSchema().example("https://api.pdv-loja.local/errors/400"))
                .addProperty("title", new StringSchema().example("Dados invalidos"))
                .addProperty("status", new IntegerSchema().example(400))
                .addProperty("detail", new StringSchema().example("Corrija os campos informados e envie a requisicao novamente."))
                .addProperty("mensagem", new StringSchema().example("Corrija os campos informados e envie a requisicao novamente."))
                .addProperty("timestamp", new StringSchema().format("date-time").example("2026-11-12T22:18:35.362Z"))
                .addProperty("detalhes", detalhes);
    }

    private Content erroContent() {
        return new Content().addMediaType("application/problem+json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse")));
    }
}

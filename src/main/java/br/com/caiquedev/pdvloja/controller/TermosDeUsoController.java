package br.com.caiquedev.pdvloja.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Termos de Uso", description = "Termos de uso da VESTIPDV API.")
public class TermosDeUsoController {

    private static final TermosDeUsoResponse TERMOS_DE_USO = new TermosDeUsoResponse(
            "Termos de Uso — VESTIPDV",
            "1.0.0",
            "Concluida",
            "Jose Caique",
            "Ao utilizar a VESTIPDV API, você concorda com os termos descritos abaixo. Esta API é mantida por Jose Caique.",
            new UsoPermitido(
                    "A API deve ser utilizada apenas para fins legítimos.",
                    List.of(
                            "Usar a API para fins ilegais ou não autorizados",
                            "Interferir no funcionamento adequado do serviço",
                            "Tentar acessar dados de outros usuários")),
            "A API é fornecida \"no estado em que se encontra\", sem garantias de qualquer tipo. "
                    + "O autor não se responsabiliza por danos decorrentes do uso ou incapacidade de uso do serviço.",
            "O código-fonte da VESTIPDV API é distribuído sob a licença MIT. Isso se aplica ao código em si, "
                    + "não ao serviço hospedado nem aos dados dos usuários.",
            new Documentacao(
                    "https://pdv-loja-roupas-api.onrender.com/swagger-ui/index.html#/",
                    "GitHub (https://github.com/caiquedevbr/pdv-loja-roupas-api/tree/main)"));

    @Operation(summary = "Consultar termos de uso", description = "Retorna somente o documento JSON dos termos de uso.")
    @ApiResponse(responseCode = "200", description = "Termos de uso retornados com sucesso.")
    @GetMapping({"/api/termos-de-uso", "/api/v2/termos-de-uso"})
    public TermosDeUsoResponse consultar() {
        return TERMOS_DE_USO;
    }

    @JsonPropertyOrder({"título", "versão", "status", "autor", "introdução", "uso_permitido",
            "isencao_responsabilidade", "licenca_codigo_fonte", "documentacao"})
    public record TermosDeUsoResponse(
            @JsonProperty("título") String titulo,
            @JsonProperty("versão") String versao,
            @JsonProperty("status") String status,
            @JsonProperty("autor") String autor,
            @JsonProperty("introdução") String introducao,
            @JsonProperty("uso_permitido") UsoPermitido usoPermitido,
            @JsonProperty("isencao_responsabilidade") String isencaoResponsabilidade,
            @JsonProperty("licenca_codigo_fonte") String licencaCodigoFonte,
            @JsonProperty("documentacao") Documentacao documentacao) {
    }

    public record UsoPermitido(
            @JsonProperty("descrição") String descricao,
            @JsonProperty("proibido") List<String> proibido) {
    }

    public record Documentacao(
            @JsonProperty("url_docs") String urlDocs,
            @JsonProperty("repositorio") String repositorio) {
    }
}

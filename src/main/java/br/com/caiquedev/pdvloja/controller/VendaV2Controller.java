package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.config.ApiKeyInterceptor;
import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.service.IdempotenciaVendaService;
import br.com.caiquedev.pdvloja.service.IdempotenciaVendaTransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v2/vendas")
@Tag(name = "Vendas", description = "Registro de vendas e baixa transacional de estoque.")
public class VendaV2Controller {

    private final IdempotenciaVendaService idempotencia;

    public VendaV2Controller(IdempotenciaVendaService idempotencia) {
        this.idempotencia = idempotencia;
    }

    @Operation(
            summary = "Registrar venda idempotente",
            description = "Cria uma venda, calcula o total pelos preços atuais e baixa uma unidade de cada produto. "
                    + "X-Idempotency-Key é obrigatório: reenvios com a mesma chave e o mesmo conteúdo devolvem a "
                    + "resposta original sem criar outra venda nem baixar o estoque novamente. Reutilizar a chave "
                    + "com conteúdo diferente retorna 409.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda criada e resposta armazenada para reprocessamentos."),
            @ApiResponse(responseCode = "400", description = "Header de idempotência ausente ou dados inválidos."),
            @ApiResponse(responseCode = "401", description = "X-API-Key ausente, inválida ou revogada."),
            @ApiResponse(responseCode = "409", description = "Chave reutilizada com outro conteúdo ou conflito de estoque."),
            @ApiResponse(responseCode = "429", description = "Limite de requisições da V2 excedido.")
    })
    @PostMapping(headers = "X-API-Version=2")
    public ResponseEntity<String> salvar(
            @RequestHeader(value = "X-Idempotency-Key", required = false)
            @Parameter(description = "Chave única por tentativa de venda; UUID é uma boa opção.",
                    example = "1d8821c9-9b52-4c58-b714-bfa0c7a71dbd", required = true)
            String chave,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cliente, forma de pagamento e produtos distintos da venda.", required = true,
                    content = @Content(schema = @Schema(implementation = Venda.class), examples = @ExampleObject(
                            value = "{\"formaPagamento\":\"PIX\",\"cliente\":{\"id\":1},\"produtos\":[{\"id\":1}]}") ))
            Venda venda,
            HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute(ApiKeyInterceptor.ATRIBUTO_USUARIO_ID);
        IdempotenciaVendaTransacaoService.Resultado resultado = idempotencia.processar(usuarioId, chave, venda);

        ResponseEntity.BodyBuilder resposta = ResponseEntity.status(resultado.statusHttp())
                .location(URI.create("/api/v2/vendas/" + resultado.vendaId()))
                .contentType(MediaTypes.HAL_JSON);
        if (resultado.reprocessado()) {
            resposta.header("Idempotent-Replayed", "true");
        }
        return resposta.body(resultado.resposta());
    }
}

package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Geração e gerenciamento de chaves de API para usuários do Sistema PDV.")
@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {

    private final ApiKeyService servicoChaves;

    public AuthController(ApiKeyService servicoChaves) {
        this.servicoChaves = servicoChaves;
    }

    @Operation(summary = "Autenticar usuário e gerar chave de API",
            description = "Valida o e-mail e a senha do usuário do PDV e gera uma chave de acesso. "
                    + "Utilize a chave no cabeçalho X-API-Key para acessar operações protegidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação realizada e chave de API gerada."),
            @ApiResponse(responseCode = "400", description = "Dados de autenticação inválidos."),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        ApiKeyService.ChaveEmitida chaveEmitida = servicoChaves.autenticar(request.email(), request.senha());
        LoginResponse resposta = new LoginResponse(chaveEmitida.valor(), chaveEmitida.usuarioId(),
                chaveEmitida.nome(), "Autenticação realizada. Utilize a chave no cabeçalho X-API-Key.");

        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(resposta);
    }

    @Operation(summary = "Revogar chave de API",
            description = "Revoga a chave informada no cabeçalho X-API-Key. Após a revogação, a chave não poderá "
                    + "mais acessar operações protegidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação de revogação processada."),
            @ApiResponse(responseCode = "401", description = "Cabeçalho X-API-Key ausente.")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Chave de API gerada na autenticação.")
            @RequestHeader(value = "X-API-Key", required = false) String chave) {
        servicoChaves.revogar(chave);
        return ResponseEntity.noContent().build();
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 254)
            @Schema(description = "E-mail cadastrado do usuário do PDV.", example = "operador@pdv.com")
            String email,
            @NotBlank
            @Schema(description = "Senha cadastrada do usuário.", example = "SenhaSegura123")
            String senha) {
    }

    public record LoginResponse(
            @Schema(description = "Chave secreta gerada para autenticação.") String apiKey,
            Long usuarioId,
            String nome,
            String mensagem) {
    }
}

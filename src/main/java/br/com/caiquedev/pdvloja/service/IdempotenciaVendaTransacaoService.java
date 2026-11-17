package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.assembler.VendaV2ModelAssembler;
import br.com.caiquedev.pdvloja.entity.RegistroIdempotencia;
import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.exception.BusinessConflictException;
import br.com.caiquedev.pdvloja.repository.RegistroIdempotenciaRepository;
import br.com.caiquedev.pdvloja.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class IdempotenciaVendaTransacaoService {

    static final String OPERACAO_CRIAR_VENDA_V2 = "POST:/api/v2/vendas";

    private final RegistroIdempotenciaRepository registros;
    private final UsuarioSistemaRepository usuarios;
    private final VendaService vendas;
    private final VendaV2ModelAssembler assembler;
    private final ObjectMapper objectMapper;
    private final int validadeHoras;

    public IdempotenciaVendaTransacaoService(RegistroIdempotenciaRepository registros,
                                              UsuarioSistemaRepository usuarios,
                                              VendaService vendas,
                                              VendaV2ModelAssembler assembler,
                                              ObjectMapper objectMapper,
                                              @Value("${app.idempotency.ttl-hours:24}") int validadeHoras) {
        this.registros = registros;
        this.usuarios = usuarios;
        this.vendas = vendas;
        this.assembler = assembler;
        this.objectMapper = objectMapper;
        if (validadeHoras < 1) {
            throw new IllegalArgumentException("app.idempotency.ttl-hours deve ser maior que zero.");
        }
        this.validadeHoras = validadeHoras;
    }

    @Transactional
    public Resultado processar(Long usuarioId, String chave, String fingerprint, Venda requisicao) {
        Instant agora = Instant.now();
        RegistroIdempotencia registro = registros
                .findByUsuarioIdAndOperacaoAndChave(usuarioId, OPERACAO_CRIAR_VENDA_V2, chave)
                .orElse(null);

        if (registro != null && registro.expirada(agora)) {
            registros.delete(registro);
            registros.flush();
            registro = null;
        }

        if (registro != null) {
            if (!registro.correspondeAo(fingerprint)) {
                throw new BusinessConflictException(
                        "A chave de idempotência já foi usada em uma requisição diferente.");
            }
            if (!registro.concluida()) {
                throw new BusinessConflictException(
                        "A requisição com esta chave de idempotência ainda está sendo processada.");
            }
            return Resultado.reprocessado(registro.getStatusHttp(), registro.getVendaId(), registro.getResposta());
        }

        UsuarioSistema usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new BusinessConflictException("Usuário autenticado não foi encontrado."));
        RegistroIdempotencia novoRegistro = registros.save(new RegistroIdempotencia(
                usuario, OPERACAO_CRIAR_VENDA_V2, chave, fingerprint,
                agora.plus(validadeHoras, ChronoUnit.HOURS)));

        Venda venda = vendas.salvar(requisicao);
        String resposta = serializarResposta(venda);
        novoRegistro.concluir(HttpStatus.CREATED.value(), venda.getId(), resposta);
        return Resultado.novo(HttpStatus.CREATED.value(), venda.getId(), resposta);
    }

    private String serializarResposta(Venda venda) {
        return assembler.paraJsonHal(venda, objectMapper);
    }

    public record Resultado(int statusHttp, Long vendaId, String resposta, boolean reprocessado) {
        static Resultado novo(int statusHttp, Long vendaId, String resposta) {
            return new Resultado(statusHttp, vendaId, resposta, false);
        }

        static Resultado reprocessado(int statusHttp, Long vendaId, String resposta) {
            return new Resultado(statusHttp, vendaId, resposta, true);
        }
    }
}

package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.exception.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class IdempotenciaVendaService {

    private static final ConcurrentHashMap<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    private final IdempotenciaVendaTransacaoService transacoes;

    public IdempotenciaVendaService(IdempotenciaVendaTransacaoService transacoes) {
        this.transacoes = transacoes;
    }

    public IdempotenciaVendaTransacaoService.Resultado processar(Long usuarioId, String chave, Venda requisicao) {
        if (chave == null || chave.isBlank()) {
            throw new InvalidRequestException("O cabeçalho X-Idempotency-Key é obrigatório para criar vendas na V2.");
        }

        String chaveNormalizada = chave.trim();
        if (chaveNormalizada.length() > 255) {
            throw new InvalidRequestException("O cabeçalho X-Idempotency-Key pode ter no máximo 255 caracteres.");
        }

        String chaveLock = usuarioId + ':' + chaveNormalizada;
        ReentrantLock lock = LOCKS.computeIfAbsent(chaveLock, ignored -> new ReentrantLock());
        lock.lock();
        try {
            return transacoes.processar(usuarioId, chaveNormalizada, fingerprint(requisicao), requisicao);
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                LOCKS.remove(chaveLock, lock);
            }
        }
    }

    private String fingerprint(Venda venda) {
        String conteudo = venda.getCliente().getId() + "|" + venda.getFormaPagamento() + "|"
                + venda.getProdutos().stream()
                .map(produto -> produto == null || produto.getId() == null ? "" : produto.getId().toString())
                .sorted()
                .reduce((esquerda, direita) -> esquerda + ',' + direita)
                .orElse("");
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(conteudo.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível.", exception);
        }
    }
}

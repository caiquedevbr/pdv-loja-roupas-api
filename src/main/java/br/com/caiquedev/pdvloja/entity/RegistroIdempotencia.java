package br.com.caiquedev.pdvloja.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "registros_idempotencia", uniqueConstraints = @UniqueConstraint(
        name = "uk_idempotencia_usuario_operacao_chave",
        columnNames = {"usuario_id", "operacao", "chave"}))
public class RegistroIdempotencia {

    public enum Status { PROCESSANDO, CONCLUIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioSistema usuario;

    @Column(nullable = false, length = 120)
    private String operacao;

    @Column(nullable = false, length = 255)
    private String chave;

    @Column(nullable = false, length = 64)
    private String fingerprint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    private Integer statusHttp;

    private Long vendaId;

    @Lob
    private String resposta;

    @Column(nullable = false, updatable = false)
    private Instant criadaEm;

    @Column(nullable = false)
    private Instant expiraEm;

    protected RegistroIdempotencia() {
    }

    public RegistroIdempotencia(UsuarioSistema usuario, String operacao, String chave,
                                String fingerprint, Instant expiraEm) {
        this.usuario = usuario;
        this.operacao = operacao;
        this.chave = chave;
        this.fingerprint = fingerprint;
        this.status = Status.PROCESSANDO;
        this.criadaEm = Instant.now();
        this.expiraEm = expiraEm;
    }

    public boolean expirada(Instant agora) {
        return !expiraEm.isAfter(agora);
    }

    public boolean correspondeAo(String outroFingerprint) {
        return fingerprint.equals(outroFingerprint);
    }

    public boolean concluida() {
        return status == Status.CONCLUIDA;
    }

    public void concluir(int statusHttp, Long vendaId, String resposta) {
        this.status = Status.CONCLUIDA;
        this.statusHttp = statusHttp;
        this.vendaId = vendaId;
        this.resposta = resposta;
    }

    public Integer getStatusHttp() {
        return statusHttp;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public String getResposta() {
        return resposta;
    }
}

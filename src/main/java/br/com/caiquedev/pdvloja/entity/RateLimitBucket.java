package br.com.caiquedev.pdvloja.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "rate_limit_buckets", uniqueConstraints = @UniqueConstraint(
        name = "uk_rate_limit_subject_window", columnNames = {"identificador", "inicio_janela"}))
public class RateLimitBucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identificador", nullable = false, length = 180)
    private String identificador;

    @Column(name = "inicio_janela", nullable = false)
    private Instant inicioJanela;

    @Column(nullable = false)
    private int requisicoes;

    protected RateLimitBucket() {
    }

    public RateLimitBucket(String identificador, Instant inicioJanela) {
        this.identificador = identificador;
        this.inicioJanela = inicioJanela;
        this.requisicoes = 0;
    }

    public int getRequisicoes() {
        return requisicoes;
    }

    public void registrarRequisicao() {
        requisicoes++;
    }
}

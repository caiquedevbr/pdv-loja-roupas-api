package br.com.caiquedev.pdvloja.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "chaves_api")
public class ChaveApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioSistema usuario;

    @Column(nullable = false, unique = true, length = 64)
    private String hash;

    @Column(nullable = false, updatable = false)
    private Instant criadaEm;

    @Column(nullable = false, updatable = false)
    private Instant expiraEm;

    private Instant revogadaEm;

    protected ChaveApi() {
    }

    public ChaveApi(UsuarioSistema usuario, String hash, Instant expiraEm) {
        this.usuario = usuario;
        this.hash = hash;
        this.criadaEm = Instant.now();
        this.expiraEm = expiraEm;
    }

    public boolean estaAtiva() {
        return revogadaEm == null && expiraEm.isAfter(Instant.now());
    }

    public void revogar() {
        this.revogadaEm = Instant.now();
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }
}

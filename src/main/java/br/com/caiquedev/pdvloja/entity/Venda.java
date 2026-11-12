package br.com.caiquedev.pdvloja.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, updatable = false)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2026-11-11T10:30:00")
    private LocalDateTime dataVenda;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(example = "PIX")
    private FormaPagamento formaPagamento;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Total calculado no servidor", example = "79.90")
    private BigDecimal valorTotal;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @Schema(description = "Informe ao menos o id de um cliente existente", example = "{\"id\": 1}")
    private Cliente cliente;

    @NotEmpty
    @ManyToMany
    @JoinTable(
            name = "venda_produto",
            joinColumns = @JoinColumn(name = "venda_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    @Schema(description = "Produtos distintos da venda; cada produto representa uma unidade", example = "[{\"id\": 1}, {\"id\": 2}]")
    private Set<Produto> produtos;

    public enum FormaPagamento { DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX }

    public Venda() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Set<Produto> getProdutos() { return produtos; }
    public void setProdutos(Set<Produto> produtos) { this.produtos = produtos; }
}

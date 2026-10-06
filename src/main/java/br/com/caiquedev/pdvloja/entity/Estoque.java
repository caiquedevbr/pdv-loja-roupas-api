package br.com.caiquedev.pdvloja.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    private Integer quantidade;

    @NotNull
    @Min(0)
    private Integer estoqueMinimo;

    @NotNull
    @OneToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public Estoque() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
}

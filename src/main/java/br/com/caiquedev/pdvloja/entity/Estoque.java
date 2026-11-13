package br.com.caiquedev.pdvloja.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Schema(name = "Estoque", description = "Saldo e nivel minimo de estoque de um produto")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotNull
    @Min(0)
    @Schema(example = "20")
    private Integer quantidade;

    @NotNull
    @Min(0)
    @Schema(example = "5")
    private Integer estoqueMinimo;

    @NotNull
    @OneToOne
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    @Schema(description = "Informe ao menos o id de um produto existente", example = "{\"id\": 1}")
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

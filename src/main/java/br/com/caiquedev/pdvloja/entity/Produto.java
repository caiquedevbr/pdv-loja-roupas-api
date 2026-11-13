package br.com.caiquedev.pdvloja.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Schema(name = "Produto", description = "Dados de um produto comercializado pela loja")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(example = "Camiseta básica")
    private String nome;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    @Schema(example = "Camiseta de algodão preta")
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Schema(description = "Preço unitário positivo", example = "79.90")
    private BigDecimal preco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(example = "M")
    private Tamanho tamanho;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @Schema(description = "Informe ao menos o id de uma categoria existente", example = "{\"id\": 1}")
    private Categoria categoria;

    public enum Tamanho { PP, P, M, G, GG }

    public Produto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Tamanho getTamanho() { return tamanho; }
    public void setTamanho(Tamanho tamanho) { this.tamanho = tamanho; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}

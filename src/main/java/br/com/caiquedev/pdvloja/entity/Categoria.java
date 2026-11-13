package br.com.caiquedev.pdvloja.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Schema(name = "Categoria", description = "Dados de uma categoria usada para classificar produtos")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true)
    @Schema(description = "Nome único da categoria", example = "Camisetas")
    private String nome;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    @Schema(example = "Camisetas e regatas para o dia a dia")
    private String descricao;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome=nome;
    }

    public String getDescricao(){
        return descricao;
    }
    public void setDescricao(String descricao){
        this.descricao=descricao;
    }
}

package br.com.caiquedev.pdvloja.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String nome;

    @NotNull
    @Min(0)
    private Integer idade;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @NotBlank
    private String telefone;

    @NotBlank
    @Column(unique = true)
    private String cpf;

    public enum Genero { MASCULINO, FEMININO, OUTRO }

    public Cliente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
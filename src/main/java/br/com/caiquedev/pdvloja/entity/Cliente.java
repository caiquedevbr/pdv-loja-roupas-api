package br.com.caiquedev.pdvloja.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Schema(name = "Cliente", description = "Dados de identificacao e contato de um cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(example = "Maria Silva")
    private String nome;

    @NotNull
    @Min(1)
    @Max(130)
    @Schema(example = "25")
    private Integer idade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(example = "FEMININO")
    private Genero genero;

    @NotBlank
    @Pattern(regexp = "\\d{10,11}", message = "deve conter 10 ou 11 dígitos")
    @Schema(description = "Telefone com 10 ou 11 dígitos, sem máscara", example = "11999999999")
    private String telefone;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "deve conter 11 dígitos")
    @Column(nullable = false, unique = true, length = 11)
    @Schema(description = "CPF com 11 dígitos, sem máscara", example = "12345678901")
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

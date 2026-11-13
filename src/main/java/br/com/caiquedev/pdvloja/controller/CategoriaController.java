package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.assembler.CategoriaModelAssembler;
import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaModelAssembler assembler;
    private final PagedResourcesAssembler<Categoria> pagedResourcesAssembler;

    public CategoriaController(CategoriaService categoriaService,
                               CategoriaModelAssembler assembler,
                               PagedResourcesAssembler<Categoria> pagedResourcesAssembler) {
        this.categoriaService = categoriaService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar todas as categorias")
    @ApiResponse(responseCode = "200", description = "Lista paginada de categorias retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Categoria>>> listarTodos(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable) {
        Page<Categoria> page = categoriaService.listarTodos(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Buscar categoria por ID")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @GetMapping("/{id}")
    public EntityModel<Categoria> listarPorId(@Parameter(description = "Identificador da categoria") @PathVariable Long id) {
        return assembler.toModel(categoriaService.listarPorId(id));
    }

    @Operation(summary = "Buscar categorias por nome")
    @ApiResponse(responseCode = "200", description = "Lista paginada de categorias retornada com sucesso")
    @GetMapping("/busca")
    public ResponseEntity<PagedModel<EntityModel<Categoria>>> listarPorNome(
            @Parameter(description = "Trecho do nome da categoria para busca", required = true) @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Categoria> page = categoriaService.listarPorNome(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Cadastrar nova categoria", description = "Cria uma categoria única para classificação de produtos.")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Nome de categoria já cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Categoria>> salvar(@RequestBody @Valid Categoria categoria) {
        EntityModel<Categoria> model = assembler.toModel(categoriaService.salvar(categoria));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Atualizar categoria", description = "Substitui os dados da categoria identificada pelo ID.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Nome de categoria já cadastrado")
    @PutMapping("/{id}")
    public EntityModel<Categoria> atualizar(@Parameter(description = "Identificador da categoria") @PathVariable Long id, @RequestBody @Valid Categoria categoria) {
        return assembler.toModel(categoriaService.atualizar(id, categoria));
    }

    @Operation(summary = "Excluir categoria")
    @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria possui produtos vinculados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Identificador da categoria") @PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

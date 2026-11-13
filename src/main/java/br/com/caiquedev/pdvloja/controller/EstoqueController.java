package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.assembler.EstoqueModelAssembler;
import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.service.EstoqueService;
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
@RequestMapping("/api/estoques")
@Tag(name = "Estoques", description = "Gerenciamento de estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final EstoqueModelAssembler assembler;
    private final PagedResourcesAssembler<Estoque> pagedResourcesAssembler;

    public EstoqueController(EstoqueService estoqueService,
                             EstoqueModelAssembler assembler,
                             PagedResourcesAssembler<Estoque> pagedResourcesAssembler) {
        this.estoqueService = estoqueService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar todos os estoques")
    @ApiResponse(responseCode = "200", description = "Lista paginada de estoques retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Estoque>>> listarTodos(
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Estoque> page = estoqueService.listarTodos(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Buscar estoque por ID")
    @ApiResponse(responseCode = "200", description = "Estoque encontrado")
    @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Estoque> listarPorId(@Parameter(description = "Identificador do estoque") @PathVariable Long id) {
        return assembler.toModel(estoqueService.listarPorId(id));
    }

    @Operation(summary = "Buscar estoque por produto", description = "Retorna o único registro de estoque associado ao produto informado.")
    @ApiResponse(responseCode = "200", description = "Estoque encontrado")
    @ApiResponse(responseCode = "404", description = "Produto sem estoque cadastrado")
    @GetMapping("/produto/{produtoId}")
    public EntityModel<Estoque> listarPorProduto(@Parameter(description = "Identificador do produto associado") @PathVariable Long produtoId) {
        return assembler.toModel(estoqueService.listarPorProduto(produtoId));
    }

    @Operation(summary = "Listar estoques com quantidade baixa")
    @ApiResponse(responseCode = "200", description = "Lista paginada de estoques baixos retornada com sucesso")
    @GetMapping("/baixo")
    public ResponseEntity<PagedModel<EntityModel<Estoque>>> listarEstoqueBaixo(
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Estoque> page = estoqueService.listarEstoqueBaixo(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Cadastrar novo estoque", description = "Cria o único registro de estoque para um produto existente.")
    @ApiResponse(responseCode = "201", description = "Estoque criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Produto já possui estoque cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Estoque>> salvar(@RequestBody @Valid Estoque estoque) {
        EntityModel<Estoque> model = assembler.toModel(estoqueService.salvar(estoque));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Atualizar estoque", description = "Atualiza quantidade, mínimo e produto do registro de estoque.")
    @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Produto já possui estoque cadastrado")
    @PutMapping("/{id}")
    public EntityModel<Estoque> atualizar(@Parameter(description = "Identificador do estoque") @PathVariable Long id, @RequestBody @Valid Estoque estoque) {
        return assembler.toModel(estoqueService.atualizar(id, estoque));
    }

    @Operation(summary = "Excluir estoque")
    @ApiResponse(responseCode = "204", description = "Estoque excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Identificador do estoque") @PathVariable Long id) {
        estoqueService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

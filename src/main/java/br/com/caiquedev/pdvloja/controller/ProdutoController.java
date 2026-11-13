package br.com.caiquedev.pdvloja.controller;
import br.com.caiquedev.pdvloja.assembler.ProdutoModelAssembler;
import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.service.ProdutoService;
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
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoModelAssembler assembler;
    private final PagedResourcesAssembler<Produto> pagedResourcesAssembler;

    public ProdutoController(ProdutoService produtoService,
                             ProdutoModelAssembler assembler,
                             PagedResourcesAssembler<Produto> pagedResourcesAssembler) {
        this.produtoService = produtoService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar todos os produtos")
    @ApiResponse(responseCode = "200", description = "Lista paginada de produtos retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Produto>>> listarTodos(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable) {
        Page<Produto> page = produtoService.listarTodos(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Produto> listarPorId(@Parameter(description = "Identificador do produto") @PathVariable Long id) {
        return assembler.toModel(produtoService.listarPorId(id));
    }

    @Operation(summary = "Buscar produtos por nome")
    @ApiResponse(responseCode = "200", description = "Lista paginada de produtos retornada com sucesso")
    @GetMapping("/busca")
    public ResponseEntity<PagedModel<EntityModel<Produto>>> listarPorNome(
            @Parameter(description = "Trecho do nome do produto para busca", required = true) @RequestParam String nome,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Produto> page = produtoService.listarPorNome(nome, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Cadastrar novo produto", description = "Cria um produto associado a uma categoria existente.")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @PostMapping
    public ResponseEntity<EntityModel<Produto>> salvar(@RequestBody @Valid Produto produto) {
        EntityModel<Produto> model = assembler.toModel(produtoService.salvar(produto));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Atualizar produto", description = "Substitui os dados do produto identificado pelo ID.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PutMapping("/{id}")
    public EntityModel<Produto> atualizar(@Parameter(description = "Identificador do produto") @PathVariable Long id, @RequestBody @Valid Produto produto) {
        return assembler.toModel(produtoService.atualizar(id, produto));
    }

    @Operation(summary = "Excluir produto")
    @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Produto possui estoque ou vendas vinculados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Identificador do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

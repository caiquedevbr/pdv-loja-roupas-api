package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.assembler.VendaModelAssembler;
import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/vendas")
@Tag(name = "Vendas", description = "Gerenciamento de vendas")
public class VendaController {

    private final VendaService vendaService;
    private final VendaModelAssembler assembler;
    private final PagedResourcesAssembler<Venda> pagedResourcesAssembler;

    public VendaController(VendaService vendaService,
                           VendaModelAssembler assembler,
                           PagedResourcesAssembler<Venda> pagedResourcesAssembler) {
        this.vendaService = vendaService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar todas as vendas")
    @ApiResponse(responseCode = "200", description = "Lista paginada de vendas retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Venda>>> listarTodos(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "dataVenda") Pageable pageable) {
        Page<Venda> page = vendaService.listarTodos(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Buscar venda por ID")
    @ApiResponse(responseCode = "200", description = "Venda encontrada")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @GetMapping("/{id}")
    public EntityModel<Venda> listarPorId(@PathVariable Long id) {
        return assembler.toModel(vendaService.listarPorId(id));
    }

    @Operation(summary = "Buscar vendas por cliente")
    @ApiResponse(responseCode = "200", description = "Lista paginada de vendas retornada com sucesso")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<PagedModel<EntityModel<Venda>>> listarPorCliente(
            @PathVariable Long clienteId,
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "dataVenda") Pageable pageable) {
        Page<Venda> page = vendaService.listarPorCliente(clienteId, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Cadastrar nova venda")
    @ApiResponse(responseCode = "201", description = "Venda criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<EntityModel<Venda>> salvar(@RequestBody @Valid Venda venda) {
        EntityModel<Venda> model = assembler.toModel(vendaService.salvar(venda));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Atualizar venda")
    @ApiResponse(responseCode = "200", description = "Venda atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @PutMapping("/{id}")
    public EntityModel<Venda> atualizar(@PathVariable Long id, @RequestBody @Valid Venda venda) {
        return assembler.toModel(vendaService.atualizar(id, venda));
    }

    @Operation(summary = "Excluir venda")
    @ApiResponse(responseCode = "204", description = "Venda excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

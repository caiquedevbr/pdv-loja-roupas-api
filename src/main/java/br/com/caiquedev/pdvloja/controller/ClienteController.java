package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.assembler.ClienteModelAssembler;
import br.com.caiquedev.pdvloja.entity.Cliente;
import br.com.caiquedev.pdvloja.service.ClienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteModelAssembler assembler;
    private final PagedResourcesAssembler<Cliente> pagedResourcesAssembler;

    public ClienteController(ClienteService clienteService,
                             ClienteModelAssembler assembler,
                             PagedResourcesAssembler<Cliente> pagedResourcesAssembler) {
        this.clienteService = clienteService;
        this.assembler = assembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar todos os clientes")
    @ApiResponse(responseCode = "200", description = "Lista paginada de clientes retornada com sucesso")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Cliente>>> listarTodos(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "nome") Pageable pageable) {
        Page<Cliente> page = clienteService.listarTodos(pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(page, assembler));
    }

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @GetMapping("/{id}")
    public EntityModel<Cliente> listarPorId(@Parameter(description = "Identificador do cliente") @PathVariable Long id) {
        return assembler.toModel(clienteService.listarPorId(id));
    }

    @Operation(summary = "Buscar cliente por CPF")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @GetMapping("/cpf/{cpf}")
    public EntityModel<Cliente> listarPorCpf(@Parameter(description = "CPF com 11 digitos, sem mascara") @PathVariable String cpf) {
        return assembler.toModel(clienteService.listarPorCpf(cpf));
    }

    @Operation(summary = "Cadastrar novo cliente", description = "Cria um cliente com CPF único, telefone e dados obrigatórios válidos.")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> salvar(@RequestBody @Valid Cliente cliente) {
        EntityModel<Cliente> model = assembler.toModel(clienteService.salvar(cliente));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Atualizar cliente", description = "Substitui os dados do cliente identificado pelo ID.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    @PutMapping("/{id}")
    public EntityModel<Cliente> atualizar(@Parameter(description = "Identificador do cliente") @PathVariable Long id, @RequestBody @Valid Cliente cliente) {
        return assembler.toModel(clienteService.atualizar(id, cliente));
    }

    @Operation(summary = "Excluir cliente")
    @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Cliente possui vendas vinculadas")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Identificador do cliente") @PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

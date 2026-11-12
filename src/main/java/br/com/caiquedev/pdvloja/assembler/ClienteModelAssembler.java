package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.ClienteController;
import br.com.caiquedev.pdvloja.controller.VendaController;
import br.com.caiquedev.pdvloja.entity.Cliente;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>> {

    @Override
    public EntityModel<Cliente> toModel(Cliente cliente) {
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).listarPorId(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).atualizar(cliente.getId(), cliente)).withRel("update"),
                linkTo(methodOn(ClienteController.class).deletar(cliente.getId())).withRel("delete"),
                linkTo(methodOn(VendaController.class).listarPorCliente(cliente.getId(), null)).withRel("vendas"),
                linkTo(methodOn(ClienteController.class).listarTodos(null)).withRel("clientes")
        );
    }
}

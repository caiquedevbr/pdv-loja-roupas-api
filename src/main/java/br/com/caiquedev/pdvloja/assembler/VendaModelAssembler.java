package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.VendaController;
import br.com.caiquedev.pdvloja.controller.ClienteController;
import br.com.caiquedev.pdvloja.controller.ProdutoController;
import br.com.caiquedev.pdvloja.entity.Venda;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class VendaModelAssembler implements RepresentationModelAssembler<Venda, EntityModel<Venda>> {

    @Override
    public EntityModel<Venda> toModel(Venda venda) {
        EntityModel<Venda> model = EntityModel.of(venda,
                linkTo(methodOn(VendaController.class).listarPorId(venda.getId())).withSelfRel(),
                linkTo(methodOn(VendaController.class).atualizar(venda.getId(), venda)).withRel("update"),
                linkTo(methodOn(VendaController.class).deletar(venda.getId())).withRel("delete"),
                linkTo(methodOn(ClienteController.class).listarPorId(venda.getCliente().getId())).withRel("cliente"),
                linkTo(methodOn(VendaController.class).listarTodos(null)).withRel("vendas")
        );
        venda.getProdutos().forEach(produto -> model.add(
                linkTo(methodOn(ProdutoController.class).listarPorId(produto.getId())).withRel("produtos")
        ));
        return model;
    }
}

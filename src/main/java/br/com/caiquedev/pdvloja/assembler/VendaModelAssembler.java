package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.VendaController;
import br.com.caiquedev.pdvloja.entity.Venda;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class VendaModelAssembler implements RepresentationModelAssembler<Venda, EntityModel<Venda>> {

    @Override
    public EntityModel<Venda> toModel(Venda venda) {
        return EntityModel.of(venda,
                linkTo(methodOn(VendaController.class).listarPorId(venda.getId())).withSelfRel(),
                linkTo(methodOn(VendaController.class).atualizar(venda.getId(), venda)).withRel("update"),
                linkTo(methodOn(VendaController.class).deletar(venda.getId())).withRel("delete"),
                linkTo(methodOn(VendaController.class).listarTodos(null)).withRel("vendas")
        );
    }
}

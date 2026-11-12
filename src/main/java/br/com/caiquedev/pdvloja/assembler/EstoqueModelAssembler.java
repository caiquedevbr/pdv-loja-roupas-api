package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.EstoqueController;
import br.com.caiquedev.pdvloja.controller.ProdutoController;
import br.com.caiquedev.pdvloja.entity.Estoque;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EstoqueModelAssembler implements RepresentationModelAssembler<Estoque, EntityModel<Estoque>> {

    @Override
    public EntityModel<Estoque> toModel(Estoque estoque) {
        return EntityModel.of(estoque,
                linkTo(methodOn(EstoqueController.class).listarPorId(estoque.getId())).withSelfRel(),
                linkTo(methodOn(EstoqueController.class).atualizar(estoque.getId(), estoque)).withRel("update"),
                linkTo(methodOn(EstoqueController.class).deletar(estoque.getId())).withRel("delete"),
                linkTo(methodOn(ProdutoController.class).listarPorId(estoque.getProduto().getId())).withRel("produto"),
                linkTo(methodOn(EstoqueController.class).listarTodos(null)).withRel("estoques")
        );
    }
}

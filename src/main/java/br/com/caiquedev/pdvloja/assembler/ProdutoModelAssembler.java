package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.ProdutoController;
import br.com.caiquedev.pdvloja.controller.CategoriaController;
import br.com.caiquedev.pdvloja.controller.EstoqueController;
import br.com.caiquedev.pdvloja.entity.Produto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProdutoModelAssembler implements RepresentationModelAssembler<Produto, EntityModel<Produto>> {

    @Override
    public EntityModel<Produto> toModel(Produto produto) {
        return EntityModel.of(produto,
                linkTo(methodOn(ProdutoController.class).listarPorId(produto.getId())).withSelfRel(),
                linkTo(methodOn(ProdutoController.class).atualizar(produto.getId(), produto)).withRel("update"),
                linkTo(methodOn(ProdutoController.class).deletar(produto.getId())).withRel("delete"),
                linkTo(methodOn(CategoriaController.class).listarPorId(produto.getCategoria().getId())).withRel("categoria"),
                linkTo(methodOn(EstoqueController.class).listarPorProduto(produto.getId())).withRel("estoque"),
                linkTo(methodOn(ProdutoController.class).listarTodos(null)).withRel("produtos")
        );
    }
}

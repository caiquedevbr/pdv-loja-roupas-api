package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.controller.CategoriaController;
import br.com.caiquedev.pdvloja.entity.Categoria;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public EntityModel<Categoria> toModel(Categoria categoria) {
        return EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).listarPorId(categoria.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).atualizar(categoria.getId(), categoria)).withRel("update"),
                linkTo(methodOn(CategoriaController.class).deletar(categoria.getId())).withRel("delete"),
                linkTo(methodOn(CategoriaController.class).listarTodos(null)).withRel("categorias")
        );
    }
}

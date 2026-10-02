package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class categoriaController {

    private final CategoriaRepository categoriaRepository;

    public categoriaController(CategoriaRepository categoriaRepository){
        this.categoriaRepository=categoriaRepository;
    }

    @PostMapping
    public Categoria save(@RequestBody Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    @PostMapping
    public Categoria atualizar(@RequestBody Categoria categoria){
        Categoria existente = categoriaRepository.findById(categoria.getId()).orElseThrow();

    }

    @GetMapping
    public List<Categoria> listar(){
        return categoriaRepository.findAll();
    }
}

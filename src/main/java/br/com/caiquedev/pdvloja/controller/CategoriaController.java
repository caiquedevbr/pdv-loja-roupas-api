package br.com.caiquedev.pdvloja.controller;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listarTodos() {
        return categoriaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Categoria listarPorId(@PathVariable Long id) {
        return categoriaService.listarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria adicionar(@RequestBody Categoria categoria) {
        return categoriaService.salvar(categoria);
    }

    @PutMapping("/{id}")
    public Categoria atualizar(
            @PathVariable Long id,
            @RequestBody Categoria categoria
    ) {
        return categoriaService.atualizar(id, categoria);
    }

    @DeleteMapping("/{id}")
    public Categoria deletar(@PathVariable Long id) {
        return categoriaService.deletar(id);
    }
}
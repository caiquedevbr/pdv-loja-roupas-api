package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria salvar(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long id, Categoria categoria){
        Categoria existente = categoriaRepository.findById(id).orElseThrow();
        existente.setNome(categoria.getNome());
        existente.setDescricao(categoria.getDescricao());

        return categoriaRepository.save(existente);
    }

    public Categoria deletar(Long id){
        Categoria existente = categoriaRepository.findById(id).orElseThrow();
        categoriaRepository.delete(existente);
        return existente;
    }

    public Categoria listarPorId(Long id){
        return categoriaRepository.findById(id).orElseThrow();
    }

    public List<Categoria> ListarTodos(){
        Categoria categoria = new Categoria();

        return categoriaRepository.findAll();
    }


}

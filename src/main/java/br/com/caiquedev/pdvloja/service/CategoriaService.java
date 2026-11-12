package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.exception.CategoriaNotFoundException;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria salvar(Categoria categoria) {
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }

    public Page<Categoria> listarTodos(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    public Categoria listarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    public Page<Categoria> listarPorNome(String nome, Pageable pageable) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Categoria atualizar(Long id, Categoria categoria) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        existente.setNome(categoria.getNome());
        existente.setDescricao(categoria.getDescricao());
        return categoriaRepository.save(existente);
    }

    public void deletar(Long id) {
        categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFoundException(id));
        categoriaRepository.deleteById(id);
    }
}

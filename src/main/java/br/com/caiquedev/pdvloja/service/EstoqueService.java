package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.exception.EstoqueNotFoundException;
import br.com.caiquedev.pdvloja.repository.EstoqueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public Estoque salvar(Estoque estoque) {
        return estoqueRepository.save(estoque);
    }

    public Page<Estoque> listarTodos(Pageable pageable) {
        return estoqueRepository.findAll(pageable);
    }

    public Estoque listarPorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado com ID: " + id));
    }

    public Page<Estoque> listarEstoqueBaixo(Pageable pageable) {
        return estoqueRepository.findByQuantidadeLessThanEqualEstoqueMinimo(pageable);
    }

    public Estoque atualizar(Long id, Estoque estoque) {
        Estoque existente = estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado com ID: " + id));
        existente.setQuantidade(estoque.getQuantidade());
        existente.setEstoqueMinimo(estoque.getEstoqueMinimo());
        existente.setProduto(estoque.getProduto());
        return estoqueRepository.save(existente);
    }

    public void deletar(Long id) {
        estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado com ID: " + id));
        estoqueRepository.deleteById(id);
    }
}

package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.exception.EstoqueNotFoundException;
import br.com.caiquedev.pdvloja.exception.InvalidRequestException;
import br.com.caiquedev.pdvloja.exception.ProdutoNotFoundException;
import br.com.caiquedev.pdvloja.repository.EstoqueRepository;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    public Estoque salvar(Estoque estoque) {
        estoque.setId(null);
        estoque.setProduto(resolverProduto(estoque.getProduto()));
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
        return estoqueRepository.findEstoqueAbaixoDoMinimo(pageable);
    }

    public Estoque listarPorProduto(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado para o produto: " + produtoId));
    }

    public Estoque atualizar(Long id, Estoque estoque) {
        Estoque existente = estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado com ID: " + id));
        existente.setQuantidade(estoque.getQuantidade());
        existente.setEstoqueMinimo(estoque.getEstoqueMinimo());
        existente.setProduto(resolverProduto(estoque.getProduto()));
        return estoqueRepository.save(existente);
    }

    public void deletar(Long id) {
        estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado com ID: " + id));
        estoqueRepository.deleteById(id);
    }

    private Produto resolverProduto(Produto produto) {
        if (produto == null || produto.getId() == null) {
            throw new InvalidRequestException("Informe o ID de um produto existente.");
        }
        return produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new ProdutoNotFoundException(produto.getId()));
    }
}

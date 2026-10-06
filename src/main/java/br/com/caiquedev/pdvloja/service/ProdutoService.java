package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.exception.ProdutoNotFoundException;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Page<Produto> listarTodos(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public Produto listarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
    }

    public Page<Produto> listarPorNome(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Produto atualizar(Long id, Produto produto) {
        Produto existente = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
        existente.setNome(produto.getNome());
        existente.setDescricao(produto.getDescricao());
        existente.setPreco(produto.getPreco());
        existente.setTamanho(produto.getTamanho());
        existente.setCategoria(produto.getCategoria());
        return produtoRepository.save(existente);
    }

    public void deletar(Long id) {
        produtoRepository.findById(id).orElseThrow(() -> new ProdutoNotFoundException(id));
        produtoRepository.deleteById(id);
    }
}

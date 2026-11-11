package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.exception.CategoriaNotFoundException;
import br.com.caiquedev.pdvloja.exception.InvalidRequestException;
import br.com.caiquedev.pdvloja.exception.ProdutoNotFoundException;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Produto salvar(Produto produto) {
        produto.setId(null);
        produto.setCategoria(resolverCategoria(produto.getCategoria()));
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
        existente.setCategoria(resolverCategoria(produto.getCategoria()));
        return produtoRepository.save(existente);
    }

    public void deletar(Long id) {
        produtoRepository.findById(id).orElseThrow(() -> new ProdutoNotFoundException(id));
        produtoRepository.deleteById(id);
    }

    private Categoria resolverCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new InvalidRequestException("Informe o ID de uma categoria existente.");
        }
        return categoriaRepository.findById(categoria.getId())
                .orElseThrow(() -> new CategoriaNotFoundException(categoria.getId()));
    }
}

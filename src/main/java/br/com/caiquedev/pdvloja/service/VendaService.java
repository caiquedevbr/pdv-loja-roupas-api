package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.entity.Cliente;
import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.exception.BusinessConflictException;
import br.com.caiquedev.pdvloja.exception.ClienteNotFoundException;
import br.com.caiquedev.pdvloja.exception.InvalidRequestException;
import br.com.caiquedev.pdvloja.exception.ProdutoNotFoundException;
import br.com.caiquedev.pdvloja.exception.VendaNotFoundException;
import br.com.caiquedev.pdvloja.repository.ClienteRepository;
import br.com.caiquedev.pdvloja.repository.EstoqueRepository;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import br.com.caiquedev.pdvloja.repository.VendaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public VendaService(VendaRepository vendaRepository,
                        ClienteRepository clienteRepository,
                        ProdutoRepository produtoRepository,
                        EstoqueRepository estoqueRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional
    public Venda salvar(Venda venda) {
        venda.setId(null);
        venda.setCliente(resolverCliente(venda.getCliente()));
        Set<Produto> produtos = resolverProdutos(venda.getProdutos());
        retirarDoEstoque(produtos);
        venda.setProdutos(produtos);
        venda.setDataVenda(LocalDateTime.now());
        venda.setValorTotal(calcularValorTotal(produtos));
        return vendaRepository.save(venda);
    }

    public Page<Venda> listarTodos(Pageable pageable) {
        return vendaRepository.findAll(pageable);
    }

    public Venda listarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));
    }

    public Page<Venda> listarPorCliente(Long clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException(clienteId);
        }
        return vendaRepository.findByClienteId(clienteId, pageable);
    }

    @Transactional
    public Venda atualizar(Long id, Venda venda) {
        Venda existente = vendaRepository.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));
        Cliente cliente = resolverCliente(venda.getCliente());
        Set<Produto> produtos = resolverProdutos(venda.getProdutos());

        devolverAoEstoque(existente.getProdutos());
        retirarDoEstoque(produtos);

        existente.setFormaPagamento(venda.getFormaPagamento());
        existente.setValorTotal(calcularValorTotal(produtos));
        existente.setCliente(cliente);
        existente.setProdutos(produtos);
        return vendaRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new VendaNotFoundException(id));
        devolverAoEstoque(venda.getProdutos());
        vendaRepository.delete(venda);
    }

    private Cliente resolverCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new InvalidRequestException("Informe o ID de um cliente existente.");
        }
        return clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new ClienteNotFoundException(cliente.getId()));
    }

    private Set<Produto> resolverProdutos(Set<Produto> produtosRecebidos) {
        if (produtosRecebidos == null || produtosRecebidos.isEmpty()) {
            throw new InvalidRequestException("Informe pelo menos um produto para a venda.");
        }

        Map<Long, Produto> produtos = new LinkedHashMap<>();
        for (Produto produto : produtosRecebidos) {
            if (produto == null || produto.getId() == null) {
                throw new InvalidRequestException("Cada produto da venda deve informar um ID existente.");
            }
            produtos.putIfAbsent(produto.getId(), produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new ProdutoNotFoundException(produto.getId())));
        }
        return new LinkedHashSet<>(produtos.values());
    }

    private void retirarDoEstoque(Set<Produto> produtos) {
        movimentarEstoque(produtos, -1);
    }

    private void devolverAoEstoque(Set<Produto> produtos) {
        movimentarEstoque(produtos, 1);
    }

    private void movimentarEstoque(Set<Produto> produtos, int variacao) {
        produtos.stream()
                .sorted(Comparator.comparing(Produto::getId))
                .forEach(produto -> {
                    Estoque estoque = estoqueRepository.findByProdutoIdForUpdate(produto.getId())
                            .orElseThrow(() -> new BusinessConflictException(
                                    "O produto " + produto.getId() + " não possui estoque cadastrado."));
                    int novaQuantidade = estoque.getQuantidade() + variacao;
                    if (novaQuantidade < 0) {
                        throw new BusinessConflictException(
                                "Estoque insuficiente para o produto " + produto.getId() + ".");
                    }
                    estoque.setQuantidade(novaQuantidade);
                });
    }

    private BigDecimal calcularValorTotal(Set<Produto> produtos) {
        return produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

package br.com.caiquedev.pdvloja;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.entity.Cliente;
import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import br.com.caiquedev.pdvloja.repository.ClienteRepository;
import br.com.caiquedev.pdvloja.repository.EstoqueRepository;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import br.com.caiquedev.pdvloja.service.VendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class VendaServiceIntegrationTests {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Test
    void deveCalcularTotalEBaixarEstoqueAoRegistrarVenda() {
        Categoria categoria = new Categoria();
        categoria.setNome("Camisetas");
        categoria.setDescricao("Camisetas para venda");
        categoria = categoriaRepository.save(categoria);

        Produto produto = new Produto();
        produto.setNome("Camiseta preta");
        produto.setDescricao("Camiseta básica preta");
        produto.setPreco(new BigDecimal("79.90"));
        produto.setTamanho(Produto.Tamanho.M);
        produto.setCategoria(categoria);
        produto = produtoRepository.save(produto);

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(2);
        estoque.setEstoqueMinimo(1);
        estoqueRepository.save(estoque);

        Cliente cliente = new Cliente();
        cliente.setNome("Maria Silva");
        cliente.setIdade(25);
        cliente.setGenero(Cliente.Genero.FEMININO);
        cliente.setTelefone("11999999999");
        cliente.setCpf("12345678901");
        cliente = clienteRepository.save(cliente);

        Venda venda = new Venda();
        venda.setCliente(referenciaCliente(cliente.getId()));
        venda.setProdutos(Set.of(referenciaProduto(produto.getId())));
        venda.setFormaPagamento(Venda.FormaPagamento.PIX);

        Venda vendaSalva = vendaService.salvar(venda);

        assertThat(vendaSalva.getDataVenda()).isNotNull();
        assertThat(vendaSalva.getValorTotal()).isEqualByComparingTo("79.90");
        assertThat(estoqueRepository.findByProdutoId(produto.getId()).orElseThrow().getQuantidade()).isEqualTo(1);

        vendaService.deletar(vendaSalva.getId());

        assertThat(estoqueRepository.findByProdutoId(produto.getId()).orElseThrow().getQuantidade()).isEqualTo(2);
    }

    private Cliente referenciaCliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }

    private Produto referenciaProduto(Long id) {
        Produto produto = new Produto();
        produto.setId(id);
        return produto;
    }
}

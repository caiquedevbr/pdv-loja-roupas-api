package br.com.caiquedev.pdvloja;

import br.com.caiquedev.pdvloja.entity.Categoria;
import br.com.caiquedev.pdvloja.entity.Cliente;
import br.com.caiquedev.pdvloja.entity.Estoque;
import br.com.caiquedev.pdvloja.entity.Produto;
import br.com.caiquedev.pdvloja.repository.CategoriaRepository;
import br.com.caiquedev.pdvloja.repository.ClienteRepository;
import br.com.caiquedev.pdvloja.repository.EstoqueRepository;
import br.com.caiquedev.pdvloja.repository.ProdutoRepository;
import br.com.caiquedev.pdvloja.repository.VendaRepository;
import br.com.caiquedev.pdvloja.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RecursosAvancadosIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ApiKeyService apiKeys;

    @Autowired
    private CategoriaRepository categorias;

    @Autowired
    private ClienteRepository clientes;

    @Autowired
    private EstoqueRepository estoques;

    @Autowired
    private ProdutoRepository produtos;

    @Autowired
    private VendaRepository vendas;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void devePreservarV1EExigirVersaoEChaveNaV2() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v2/produtos"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v2/produtos").header("X-API-Version", "2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirPreflightCorsComHeadersAvancados() throws Exception {
        mockMvc.perform(options("/api/v2/vendas")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "X-API-Version,X-API-Key,X-Idempotency-Key"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void deveReproduzirVendaIdempotenteSemNovaBaixaDeEstoque() throws Exception {
        CenarioVenda cenario = criarCenario();
        String chaveApi = apiKeys.autenticar("operador@pdv.com", "SenhaSegura123").valor();
        String chaveIdempotencia = UUID.randomUUID().toString();
        long vendasAntes = vendas.count();
        String corpo = corpoVenda(cenario);

        MvcResult primeira = enviarVenda(chaveApi, chaveIdempotencia, corpo)
                .andExpect(status().isCreated())
                .andExpect(header().doesNotExist("Idempotent-Replayed"))
                .andExpect(jsonPath("$._links.self.href").isString())
                .andReturn();

        MvcResult repetida = enviarVenda(chaveApi, chaveIdempotencia, corpo)
                .andExpect(status().isCreated())
                .andExpect(header().string("Idempotent-Replayed", "true"))
                .andReturn();

        assertThat(repetida.getResponse().getContentAsString())
                .isEqualTo(primeira.getResponse().getContentAsString());
        assertThat(vendas.count()).isEqualTo(vendasAntes + 1);
        assertThat(estoques.findById(cenario.estoqueId()).orElseThrow().getQuantidade()).isEqualTo(1);

        enviarVenda(chaveApi, chaveIdempotencia, corpoVenda(cenario, "CARTAO_CREDITO"))
                .andExpect(status().isConflict());
    }

    @Test
    void deveProcessarUmaUnicaVendaQuandoAMesmaChaveChegaEmParalelo() throws Exception {
        CenarioVenda cenario = criarCenario();
        String chaveApi = apiKeys.autenticar("operador@pdv.com", "SenhaSegura123").valor();
        String chaveIdempotencia = UUID.randomUUID().toString();
        String corpo = corpoVenda(cenario);
        long vendasAntes = vendas.count();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<MvcResult> primeira = executor.submit(() -> enviarVenda(chaveApi, chaveIdempotencia, corpo).andReturn());
            Future<MvcResult> segunda = executor.submit(() -> enviarVenda(chaveApi, chaveIdempotencia, corpo).andReturn());

            List<Integer> status = List.of(primeira.get().getResponse().getStatus(), segunda.get().getResponse().getStatus());
            assertThat(status).containsExactlyInAnyOrder(201, 201);
            assertThat(vendas.count()).isEqualTo(vendasAntes + 1);
            assertThat(estoques.findById(cenario.estoqueId()).orElseThrow().getQuantidade()).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    private org.springframework.test.web.servlet.ResultActions enviarVenda(String chaveApi, String chaveIdempotencia,
                                                                             String corpo) throws Exception {
        return mockMvc.perform(post("/api/v2/vendas")
                .header("X-API-Version", "2")
                .header("X-API-Key", chaveApi)
                .header("X-Idempotency-Key", chaveIdempotencia)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    private CenarioVenda criarCenario() {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria " + sufixo);
        categoria.setDescricao("Categoria de teste " + sufixo);
        categoria = categorias.save(categoria);

        Produto produto = new Produto();
        produto.setNome("Produto " + sufixo);
        produto.setDescricao("Produto de teste " + sufixo);
        produto.setPreco(new BigDecimal("79.90"));
        produto.setTamanho(Produto.Tamanho.M);
        produto.setCategoria(categoria);
        produto = produtos.save(produto);

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(2);
        estoque.setEstoqueMinimo(1);
        estoque = estoques.save(estoque);

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente " + sufixo);
        cliente.setIdade(30);
        cliente.setGenero(Cliente.Genero.OUTRO);
        cliente.setTelefone("11999999999");
        cliente.setCpf(String.format("%011d", Math.floorMod(System.nanoTime(), 100_000_000_000L)));
        cliente = clientes.save(cliente);

        return new CenarioVenda(cliente.getId(), produto.getId(), estoque.getId());
    }

    private String corpoVenda(CenarioVenda cenario) {
        return corpoVenda(cenario, "PIX");
    }

    private String corpoVenda(CenarioVenda cenario, String formaPagamento) {
        return "{\"formaPagamento\":\"" + formaPagamento + "\",\"cliente\":{\"id\":" + cenario.clienteId()
                + "},\"produtos\":[{\"id\":" + cenario.produtoId() + "}]}";
    }

    private record CenarioVenda(Long clienteId, Long produtoId, Long estoqueId) {
    }
}

package br.com.caiquedev.pdvloja;

import br.com.caiquedev.pdvloja.entity.ChaveApi;
import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import br.com.caiquedev.pdvloja.repository.ChaveApiRepository;
import br.com.caiquedev.pdvloja.repository.UsuarioSistemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthControllerIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioSistemaRepository usuarios;

    @Autowired
    private ChaveApiRepository chaves;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        chaves.deleteAll();
        usuarios.deleteAll();
        usuarios.save(new UsuarioSistema("Operador PDV", "operador@pdv.com",
                passwordEncoder.encode("SenhaSegura123")));
    }

    @Test
    void deveAutenticarEmitirChaveEImpediLaDeSerArmazenadaEmTextoPuro() throws Exception {
        String response = mockMvc.perform(post("/api/v2/auth/login")
                        .header("X-API-Version", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"operador@pdv.com\",\"senha\":\"SenhaSegura123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.apiKey").value(org.hamcrest.Matchers.startsWith("pdv_")))
                .andExpect(jsonPath("$.usuarioId").isNumber())
                .andReturn().getResponse().getContentAsString();

        String apiKey = extrairApiKey(response);
        assertThat(apiKey).startsWith("pdv_");
        assertThat(chaves.findAll()).hasSize(1);
        ChaveApi chaveArmazenada = chaves.findAll().get(0);
        assertThat(chaveArmazenada.estaAtiva()).isTrue();
        assertThat(chaveArmazenada.getExpiraEm()).isAfter(Instant.now().plus(23, ChronoUnit.HOURS));
        assertThat(chaves.findByHash(apiKey)).isEmpty();
    }

    @Test
    void deveRecusarChaveExpirada() throws Exception {
        UsuarioSistema usuario = usuarios.findByEmailIgnoreCase("operador@pdv.com").orElseThrow();
        String chaveExpirada = "pdv_chave-expirada";
        chaves.save(new ChaveApi(usuario, gerarHash(chaveExpirada), Instant.now().minusSeconds(1)));

        mockMvc.perform(get("/api/v2/produtos")
                        .header("X-API-Version", "2")
                        .header("X-API-Key", chaveExpirada))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveExigirChaveParaDeleteDaV2SemAfetarDeleteDaV1() throws Exception {
        mockMvc.perform(delete("/api/v2/produtos/1").header("X-API-Version", "2"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRevogarChaveImpedindoNovoDeleteV2() throws Exception {
        String apiKey = extrairApiKey(mockMvc.perform(post("/api/v2/auth/login")
                        .header("X-API-Version", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"operador@pdv.com\",\"senha\":\"SenhaSegura123\"}"))
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(post("/api/v2/auth/logout")
                        .header("X-API-Version", "2")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v2/produtos/1")
                        .header("X-API-Version", "2")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveDocumentarAChaveDeApiParaTodosOsEndpointsProtegidosDaV2() throws Exception {
        mockMvc.perform(get("/v3/api-docs/v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.description").value(org.hamcrest.Matchers.containsString(
                        "Clique em **Authorize**")))
                .andExpect(jsonPath("$.info.description").value(org.hamcrest.Matchers.containsString(
                        "Ordem recomendada de cadastro")))
                .andExpect(jsonPath("$.info.description").value(org.hamcrest.Matchers.containsString(
                        "/api/v2/vendas")))
                .andExpect(jsonPath("$.components.securitySchemes.apiKeyAuth.name").value("X-API-Key"))
                .andExpect(jsonPath("$.components.securitySchemes.apiKeyAuth.description").value(
                        org.hamcrest.Matchers.containsString("X-API-Key")))
                .andExpect(jsonPath("$.paths['/api/v2/produtos/{id}'].delete.security").isArray())
                .andExpect(jsonPath("$.paths['/api/v2/produtos'].get.security").isArray())
                .andExpect(jsonPath("$.paths['/api/v2/auth/logout'].post.security").isArray())
                .andExpect(jsonPath("$.paths['/api/v2/auth/login'].post.security").doesNotExist());
    }

    @Test
    void deveDocumentarApenasORoteamentoCanonicoDaV1() throws Exception {
        mockMvc.perform(get("/v3/api-docs/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/vendas/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/vendas/{id}']").doesNotExist());
    }

    private String extrairApiKey(String response) {
        return response.replaceAll(".*\\\"apiKey\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }

    private String gerarHash(String valor) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(valor.getBytes(StandardCharsets.UTF_8)));
    }
}

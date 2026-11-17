package br.com.caiquedev.pdvloja;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TermosDeUsoIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void deveRetornarOsTermosEmJsonNasRotasV1EV2() throws Exception {
        validarTermos("/api/termos-de-uso");
        validarTermos("/api/v2/termos-de-uso");
    }

    @Test
    void deveExporLinksDeTermosCorretosNasEspecificacoesSwagger() throws Exception {
        mockMvc.perform(get("/v3/api-docs/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.termsOfService")
                        .value("/api/termos-de-uso"));

        mockMvc.perform(get("/v3/api-docs/v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.termsOfService")
                        .value("/api/v2/termos-de-uso"));
    }

    private void validarTermos(String rota) throws Exception {
        mockMvc.perform(get(rota))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$['título']").value("Termos de Uso — VESTIPDV"))
                .andExpect(jsonPath("$['versão']").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("Concluida"))
                .andExpect(jsonPath("$.autor").value("Jose Caique"))
                .andExpect(jsonPath("$['introdução']").value(
                        "Ao utilizar a VESTIPDV API, você concorda com os termos descritos abaixo. "
                                + "Esta API é mantida por Jose Caique."))
                .andExpect(jsonPath("$['uso_permitido']['descrição']")
                        .value("A API deve ser utilizada apenas para fins legítimos."))
                .andExpect(jsonPath("$['uso_permitido'].proibido[0]")
                        .value("Usar a API para fins ilegais ou não autorizados"))
                .andExpect(jsonPath("$['uso_permitido'].proibido[1]")
                        .value("Interferir no funcionamento adequado do serviço"))
                .andExpect(jsonPath("$['uso_permitido'].proibido[2]")
                        .value("Tentar acessar dados de outros usuários"))
                .andExpect(jsonPath("$.isencao_responsabilidade").value(
                        "A API é fornecida \"no estado em que se encontra\", sem garantias de qualquer tipo. "
                                + "O autor não se responsabiliza por danos decorrentes do uso ou incapacidade de uso do serviço."))
                .andExpect(jsonPath("$.licenca_codigo_fonte").value(
                        "O código-fonte da VESTIPDV API é distribuído sob a licença MIT. "
                                + "Isso se aplica ao código em si, não ao serviço hospedado nem aos dados dos usuários."))
                .andExpect(jsonPath("$.documentacao.url_docs")
                        .value("https://pdv-loja-roupas-api.onrender.com/swagger-ui/index.html#/"))
                .andExpect(jsonPath("$.documentacao.repositorio")
                        .value("GitHub (https://github.com/caiquedevbr/pdv-loja-roupas-api/tree/main)"));
    }
}

package br.com.caiquedev.pdvloja;

import br.com.caiquedev.pdvloja.repository.RateLimitBucketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.rate-limit.requests-per-minute=2")
class RateLimitIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RateLimitBucketRepository buckets;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        buckets.deleteAll();
    }

    @Test
    void deveRetornar429EHeadersQuandoOLimiteDaV2ForExcedido() throws Exception {
        for (int tentativa = 0; tentativa < 2; tentativa++) {
            mockMvc.perform(login())
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-RateLimit-Limit", "2"));
        }

        mockMvc.perform(login())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-RateLimit-Limit", "2"))
                .andExpect(header().string("X-RateLimit-Remaining", "0"))
                .andExpect(header().exists("X-RateLimit-Reset"))
                .andExpect(header().exists("Retry-After"));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder login() {
        return post("/api/v2/auth/login")
                .header("X-API-Version", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"operador@pdv.com\",\"senha\":\"SenhaSegura123\"}");
    }
}

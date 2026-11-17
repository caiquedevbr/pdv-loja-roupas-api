package br.com.caiquedev.pdvloja;

import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import br.com.caiquedev.pdvloja.repository.UsuarioSistemaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DevUserInitializerIntegrationTests {

    @Autowired
    private UsuarioSistemaRepository usuarios;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCriarUsuarioInicialComSenhaCodificadaNoPerfilDev() {
        UsuarioSistema usuario = usuarios.findByEmailIgnoreCase("operador@pdv.com").orElseThrow();

        assertThat(usuario.getNome()).isEqualTo("Operador PDV");
        assertThat(passwordEncoder.matches("SenhaSegura123", usuario.getSenhaHash())).isTrue();
    }
}

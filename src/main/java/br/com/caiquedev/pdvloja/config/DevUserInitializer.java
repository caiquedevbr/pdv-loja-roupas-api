package br.com.caiquedev.pdvloja.config;

import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import br.com.caiquedev.pdvloja.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevUserInitializer implements ApplicationRunner {

    private final UsuarioSistemaRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String email;
    private final String senha;

    public DevUserInitializer(UsuarioSistemaRepository usuarios,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.bootstrap-user.nome}") String nome,
                              @Value("${app.bootstrap-user.email}") String email,
                              @Value("${app.bootstrap-user.senha}") String senha) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        usuarios.findByEmailIgnoreCase(email).orElseGet(() ->
                usuarios.save(new UsuarioSistema(nome, email, passwordEncoder.encode(senha))));
    }
}

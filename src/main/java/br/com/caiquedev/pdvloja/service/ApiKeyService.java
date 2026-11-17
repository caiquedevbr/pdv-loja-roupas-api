package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.ChaveApi;
import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import br.com.caiquedev.pdvloja.repository.ChaveApiRepository;
import br.com.caiquedev.pdvloja.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class ApiKeyService {

    private final UsuarioSistemaRepository usuarios;
    private final ChaveApiRepository chaves;
    private final PasswordEncoder passwordEncoder;
    private final int validadeHoras;
    private final SecureRandom random = new SecureRandom();

    public ApiKeyService(UsuarioSistemaRepository usuarios, ChaveApiRepository chaves,
                         PasswordEncoder passwordEncoder,
                         @Value("${app.api-key.ttl-hours:24}") int validadeHoras) {
        this.usuarios = usuarios;
        this.chaves = chaves;
        this.passwordEncoder = passwordEncoder;
        if (validadeHoras < 1) {
            throw new IllegalArgumentException("app.api-key.ttl-hours deve ser maior que zero.");
        }
        this.validadeHoras = validadeHoras;
    }

    @Transactional
    public ChaveEmitida autenticar(String email, String senha) {
        UsuarioSistema usuario = usuarios.findByEmailIgnoreCase(email.trim())
                .orElseThrow(this::naoAutorizado);

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw naoAutorizado();
        }

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String chave = "pdv_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        chaves.save(new ChaveApi(usuario, gerarHash(chave),
                Instant.now().plus(validadeHoras, ChronoUnit.HOURS)));
        return new ChaveEmitida(chave, usuario.getId(), usuario.getNome());
    }

    @Transactional(readOnly = true)
    public UsuarioSistema validar(String chave) {
        if (chave == null || chave.isBlank()) {
            throw naoAutorizado();
        }

        ChaveApi chaveEncontrada = chaves.findByHash(gerarHash(chave))
                .orElseThrow(this::naoAutorizado);

        if (!chaveEncontrada.estaAtiva()) {
            throw naoAutorizado();
        }

        return chaveEncontrada.getUsuario();
    }

    @Transactional
    public void revogar(String chave) {
        if (chave == null || chave.isBlank()) {
            throw naoAutorizado();
        }

        ChaveApi chaveEncontrada = chaves.findByHash(gerarHash(chave)).orElseThrow(this::naoAutorizado);
        if (!chaveEncontrada.estaAtiva()) {
            throw naoAutorizado();
        }
        chaveEncontrada.revogar();
    }

    private String gerarHash(String valor) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível.", exception);
        }
    }

    private ResponseStatusException naoAutorizado() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Credenciais ou chave de API inválidas.");
    }

    public record ChaveEmitida(String valor, Long usuarioId, String nome) {
    }
}

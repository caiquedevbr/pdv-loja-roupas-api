package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.RegistroIdempotencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistroIdempotenciaRepository extends JpaRepository<RegistroIdempotencia, Long> {

    Optional<RegistroIdempotencia> findByUsuarioIdAndOperacaoAndChave(Long usuarioId, String operacao, String chave);
}

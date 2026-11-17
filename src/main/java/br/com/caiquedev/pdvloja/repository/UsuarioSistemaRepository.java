package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {

    Optional<UsuarioSistema> findByEmailIgnoreCase(String email);
}

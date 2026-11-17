package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.ChaveApi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChaveApiRepository extends JpaRepository<ChaveApi, Long> {

    Optional<ChaveApi> findByHash(String hash);
}

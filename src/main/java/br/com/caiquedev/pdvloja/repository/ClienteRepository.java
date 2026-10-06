package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
}

package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    Page<Venda> findByClienteId(Long clienteId, Pageable pageable);
}

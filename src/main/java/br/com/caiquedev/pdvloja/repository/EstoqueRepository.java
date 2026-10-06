package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Page<Estoque> findByQuantidadeLessThanEqualEstoqueMinimo(Pageable pageable);
}

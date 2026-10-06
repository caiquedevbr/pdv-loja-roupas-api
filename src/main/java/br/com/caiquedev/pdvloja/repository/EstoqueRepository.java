package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Query("""
        SELECT e
        FROM Estoque e
        WHERE e.quantidade <= e.estoqueMinimo
    """)
    Page<Estoque> findEstoqueAbaixoDoMinimo(Pageable pageable);
}
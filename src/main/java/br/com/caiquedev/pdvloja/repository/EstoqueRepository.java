package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Query("""
        SELECT e
        FROM Estoque e
        WHERE e.quantidade <= e.estoqueMinimo
    """)
    Page<Estoque> findEstoqueAbaixoDoMinimo(Pageable pageable);

    Optional<Estoque> findByProdutoId(Long produtoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Estoque e WHERE e.produto.id = :produtoId")
    Optional<Estoque> findByProdutoIdForUpdate(@Param("produtoId") Long produtoId);
}

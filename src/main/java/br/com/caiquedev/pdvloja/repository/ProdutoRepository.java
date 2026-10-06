package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}

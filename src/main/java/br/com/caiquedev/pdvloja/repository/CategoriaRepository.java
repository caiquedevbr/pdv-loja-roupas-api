package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}

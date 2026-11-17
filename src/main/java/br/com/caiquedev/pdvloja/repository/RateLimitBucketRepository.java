package br.com.caiquedev.pdvloja.repository;

import br.com.caiquedev.pdvloja.entity.RateLimitBucket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RateLimitBucketRepository extends JpaRepository<RateLimitBucket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RateLimitBucket> findByIdentificadorAndInicioJanela(@Param("identificador") String identificador,
                                                                  @Param("inicioJanela") Instant inicioJanela);
}

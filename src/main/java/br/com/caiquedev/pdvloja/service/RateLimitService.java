package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.RateLimitBucket;
import br.com.caiquedev.pdvloja.exception.RateLimitExceededException;
import br.com.caiquedev.pdvloja.repository.RateLimitBucketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RateLimitService {

    private static final ConcurrentHashMap<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    private final RateLimitBucketRepository buckets;
    private final int limitePorMinuto;

    public RateLimitService(RateLimitBucketRepository buckets,
                            @Value("${app.rate-limit.requests-per-minute:60}") int limitePorMinuto) {
        this.buckets = buckets;
        if (limitePorMinuto < 1) {
            throw new IllegalArgumentException("app.rate-limit.requests-per-minute deve ser maior que zero.");
        }
        this.limitePorMinuto = limitePorMinuto;
    }

    @Transactional
    public Limite consumir(String identificador) {
        Instant inicioJanela = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant resetEm = inicioJanela.plus(1, ChronoUnit.MINUTES);
        String chaveLock = identificador + ':' + inicioJanela;
        ReentrantLock lock = LOCKS.computeIfAbsent(chaveLock, ignored -> new ReentrantLock());

        lock.lock();
        try {
            RateLimitBucket bucket = buckets.findByIdentificadorAndInicioJanela(identificador, inicioJanela)
                    .orElseGet(() -> buckets.save(new RateLimitBucket(identificador, inicioJanela)));

            if (bucket.getRequisicoes() >= limitePorMinuto) {
                throw new RateLimitExceededException(limitePorMinuto, resetEm);
            }

            bucket.registrarRequisicao();
            return new Limite(limitePorMinuto, limitePorMinuto - bucket.getRequisicoes(), resetEm);
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                LOCKS.remove(chaveLock, lock);
            }
        }
    }

    public record Limite(int total, int restante, Instant resetEm) {
        public long segundosAteReset() {
            return Math.max(1, Duration.between(Instant.now(), resetEm).toSeconds());
        }
    }
}

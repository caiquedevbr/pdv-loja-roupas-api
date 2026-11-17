package br.com.caiquedev.pdvloja.exception;

import java.time.Instant;

public class RateLimitExceededException extends RuntimeException {

    private final Instant resetEm;
    private final int limite;

    public RateLimitExceededException(int limite, Instant resetEm) {
        super("Limite de requisições excedido. Tente novamente após " + resetEm + ".");
        this.limite = limite;
        this.resetEm = resetEm;
    }

    public Instant getResetEm() {
        return resetEm;
    }

    public int getLimite() {
        return limite;
    }
}

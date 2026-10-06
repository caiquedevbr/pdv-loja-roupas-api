package br.com.caiquedev.pdvloja.exception;

public class EstoqueNotFoundException extends RuntimeException {

    public EstoqueNotFoundException(String message) {
        super(message);
    }
}

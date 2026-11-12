package br.com.caiquedev.pdvloja.exception;

public class EstoqueNotFoundException extends ResourceNotFoundException {

    public EstoqueNotFoundException(String message) {
        super(message);
    }
}

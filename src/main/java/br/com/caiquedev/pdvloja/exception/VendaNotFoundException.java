package br.com.caiquedev.pdvloja.exception;

public class VendaNotFoundException extends RuntimeException {

    public VendaNotFoundException(Long id) {
        super("Venda não encontrada com ID: " + id);
    }
}

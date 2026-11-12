package br.com.caiquedev.pdvloja.exception;

public class VendaNotFoundException extends ResourceNotFoundException {

    public VendaNotFoundException(Long id) {
        super("Venda não encontrada com ID: " + id);
    }
}

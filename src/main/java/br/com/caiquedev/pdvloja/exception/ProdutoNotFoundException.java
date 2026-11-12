package br.com.caiquedev.pdvloja.exception;

public class ProdutoNotFoundException extends ResourceNotFoundException {

    public ProdutoNotFoundException(Long id) {
        super("Produto não encontrado com ID: " + id);
    }
}

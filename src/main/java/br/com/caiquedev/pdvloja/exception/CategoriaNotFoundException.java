package br.com.caiquedev.pdvloja.exception;

public class CategoriaNotFoundException extends RuntimeException {

    public CategoriaNotFoundException(Long id) {
        super("Categoria não encontrada com ID: " + id);
    }
}

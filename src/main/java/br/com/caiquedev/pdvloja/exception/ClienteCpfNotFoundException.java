package br.com.caiquedev.pdvloja.exception;

public class ClienteCpfNotFoundException extends RuntimeException {

    public ClienteCpfNotFoundException(String cpf) {
        super("Cliente não encontrado com ID: " + cpf);
    }
}

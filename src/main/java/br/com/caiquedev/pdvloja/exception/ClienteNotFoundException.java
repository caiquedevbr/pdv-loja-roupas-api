package br.com.caiquedev.pdvloja.exception;

    public class ClienteNotFoundException extends RuntimeException {

        public ClienteNotFoundException(Long id) {
            super("Cliente não encontrado com ID: " + id);
        }
}

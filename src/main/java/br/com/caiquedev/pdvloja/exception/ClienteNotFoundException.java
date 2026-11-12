package br.com.caiquedev.pdvloja.exception;

    public class ClienteNotFoundException extends ResourceNotFoundException {

        public ClienteNotFoundException(Long id) {
            super("Cliente não encontrado com ID: " + id);
        }
}

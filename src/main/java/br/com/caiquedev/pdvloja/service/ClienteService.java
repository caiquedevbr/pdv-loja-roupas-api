package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Cliente;
import br.com.caiquedev.pdvloja.exception.ClienteCpfNotFoundException;
import br.com.caiquedev.pdvloja.exception.ClienteNotFoundException;
import br.com.caiquedev.pdvloja.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente salvar(Cliente cliente) {
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    public Page<Cliente> listarTodos(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Cliente listarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    public Cliente listarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteCpfNotFoundException(cpf));
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        existente.setNome(cliente.getNome());
        existente.setIdade(cliente.getIdade());
        existente.setGenero(cliente.getGenero());
        existente.setTelefone(cliente.getTelefone());
        existente.setCpf(cliente.getCpf());
        return clienteRepository.save(existente);
    }

    public void deletar(Long id) {
        clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFoundException(id));;
        clienteRepository.deleteById(id);
    }
}

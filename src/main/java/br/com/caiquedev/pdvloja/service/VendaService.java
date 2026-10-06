package br.com.caiquedev.pdvloja.service;

import br.com.caiquedev.pdvloja.entity.Venda;
import br.com.caiquedev.pdvloja.exception.VendaNotFoundException;
import br.com.caiquedev.pdvloja.repository.VendaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;

    public VendaService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    public Venda salvar(Venda venda) {
        return vendaRepository.save(venda);
    }

    public Page<Venda> listarTodos(Pageable pageable) {
        return vendaRepository.findAll(pageable);
    }

    public Venda listarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));
    }

    public Page<Venda> listarPorCliente(Long clienteId, Pageable pageable) {
        return vendaRepository.findByClienteId(clienteId, pageable);
    }

    public Venda atualizar(Long id, Venda venda) {
        Venda existente = vendaRepository.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));
        existente.setDataVenda(venda.getDataVenda());
        existente.setFormaPagamento(venda.getFormaPagamento());
        existente.setValorTotal(venda.getValorTotal());
        existente.setCliente(venda.getCliente());
        existente.setProdutos(venda.getProdutos());
        return vendaRepository.save(existente);
    }

    public void deletar(Long id) {
        vendaRepository.findById(id).orElseThrow(() -> new VendaNotFoundException(id));
        vendaRepository.deleteById(id);
    }
}

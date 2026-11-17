package br.com.caiquedev.pdvloja.assembler;

import br.com.caiquedev.pdvloja.entity.Venda;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Component
public class VendaV2ModelAssembler {

    public EntityModel<Venda> toModel(Venda venda) {
        EntityModel<Venda> model = EntityModel.of(venda,
                link("/api/v2/vendas/" + venda.getId()).withSelfRel(),
                link("/api/v2/vendas/" + venda.getId()).withRel("update"),
                link("/api/v2/vendas/" + venda.getId()).withRel("delete"),
                link("/api/v2/clientes/" + venda.getCliente().getId()).withRel("cliente"),
                link("/api/v2/vendas").withRel("vendas"));

        venda.getProdutos().forEach(produto -> model.add(
                link("/api/v2/produtos/" + produto.getId()).withRel("produtos")));
        return model;
    }

    private Link link(String caminho) {
        return Link.of(ServletUriComponentsBuilder.fromCurrentContextPath().path(caminho).toUriString());
    }

    public String paraJsonHal(Venda venda, ObjectMapper objectMapper) {
        EntityModel<Venda> model = toModel(venda);
        ObjectNode resposta = objectMapper.createObjectNode();
        resposta.put("id", venda.getId());
        resposta.put("dataVenda", venda.getDataVenda().toString());
        resposta.put("formaPagamento", venda.getFormaPagamento().name());
        resposta.put("valorTotal", venda.getValorTotal());

        ObjectNode cliente = resposta.putObject("cliente");
        cliente.put("id", venda.getCliente().getId());
        cliente.put("nome", venda.getCliente().getNome());
        cliente.put("idade", venda.getCliente().getIdade());
        cliente.put("genero", venda.getCliente().getGenero().name());
        cliente.put("telefone", venda.getCliente().getTelefone());
        cliente.put("cpf", venda.getCliente().getCpf());

        ArrayNode produtos = resposta.putArray("produtos");
        venda.getProdutos().forEach(produto -> {
            ObjectNode produtoJson = produtos.addObject();
            produtoJson.put("id", produto.getId());
            produtoJson.put("nome", produto.getNome());
            produtoJson.put("descricao", produto.getDescricao());
            produtoJson.put("preco", produto.getPreco());
            produtoJson.put("tamanho", produto.getTamanho().name());

            ObjectNode categoria = produtoJson.putObject("categoria");
            categoria.put("id", produto.getCategoria().getId());
            categoria.put("nome", produto.getCategoria().getNome());
            categoria.put("descricao", produto.getCategoria().getDescricao());
        });

        ObjectNode links = resposta.putObject("_links");
        model.getLinks().forEach(link -> adicionarLink(links, link));
        return objectMapper.writeValueAsString(resposta);
    }

    private void adicionarLink(ObjectNode links, Link link) {
        String relacao = link.getRel().value();
        ObjectNode linkJson = links.objectNode();
        linkJson.put("href", link.getHref());
        JsonNode existente = links.get(relacao);
        if (existente == null) {
            links.set(relacao, linkJson);
        } else if (existente instanceof ArrayNode array) {
            array.add(linkJson);
        } else {
            ArrayNode array = links.putArray(relacao);
            array.add(existente);
            array.add(linkJson);
        }
    }
}

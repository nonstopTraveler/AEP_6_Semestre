package com.aep.ler.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testConstrutorEGettersSetters() {
        Endereco endereco = new Endereco(
                "Rua das Flores", "123", "Casa", "Centro",
                "Barbosa Ferraz", "PR", "86960-000"
        );

        Usuario usuario = new Usuario(
                "Maria Silva", "maria@email.com", "44999999999",
                "Barbosa Ferraz", "PR", List.of(endereco)
        );

        usuario.setId("1");

        assertEquals("1", usuario.getId());
        assertEquals("Maria Silva", usuario.getNome());
        assertEquals("maria@email.com", usuario.getEmail());
        assertEquals("44999999999", usuario.getTelefone());
        assertEquals("Barbosa Ferraz", usuario.getCidade());
        assertEquals("PR", usuario.getEstado());
        assertEquals(1, usuario.getEnderecos().size());
        assertEquals("Rua das Flores", usuario.getEnderecos().get(0).getRua());
    }

    @Test
    void testEnderecoGettersSetters() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua A");
        endereco.setNumero("10");
        endereco.setComplemento("Fundos");
        endereco.setBairro("Centro");
        endereco.setCidade("Maringá");
        endereco.setEstado("PR");
        endereco.setCep("87000-000");

        assertEquals("Rua A", endereco.getRua());
        assertEquals("10", endereco.getNumero());
        assertEquals("Fundos", endereco.getComplemento());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("Maringá", endereco.getCidade());
        assertEquals("PR", endereco.getEstado());
        assertEquals("87000-000", endereco.getCep());
    }
}
package com.aep.ler.controller;

import com.aep.ler.model.Usuario;
import com.aep.ler.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId("1");
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@email.com");
    }

    @Test
    void testCadastrar() {
        when(usuarioService.cadastrar(usuario)).thenReturn(usuario);

        ResponseEntity<?> resposta = usuarioController.cadastrar(usuario);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(usuario, resposta.getBody());
    }

    @Test
    void testCadastrarEmailDuplicado() {
        when(usuarioService.cadastrar(usuario))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado"));

        ResponseEntity<?> resposta = usuarioController.cadastrar(usuario);

        assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
    }

    @Test
    void testListarTodos() {
        when(usuarioService.listarTodos()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioController.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Maria Silva", resultado.get(0).getNome());
    }

    @Test
    void testBuscarPorId() {
        when(usuarioService.buscarPorId("1")).thenReturn(usuario);

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId("1");

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(usuario, resposta.getBody());
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(usuarioService.buscarPorId("99")).thenReturn(null);

        ResponseEntity<Usuario> resposta = usuarioController.buscarPorId("99");

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void testAtualizar() {
        when(usuarioService.atualizar("1", usuario)).thenReturn(usuario);

        ResponseEntity<?> resposta = usuarioController.atualizar("1", usuario);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(usuario, resposta.getBody());
    }

    @Test
    void testAtualizarNaoEncontrado() {
        when(usuarioService.atualizar("99", usuario)).thenReturn(null);

        ResponseEntity<?> resposta = usuarioController.atualizar("99", usuario);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void testExcluir() {
        when(usuarioService.excluir("1")).thenReturn(true);

        ResponseEntity<Void> resposta = usuarioController.excluir("1");

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(usuarioService).excluir("1");
    }

    @Test
    void testExcluirNaoEncontrado() {
        when(usuarioService.excluir("99")).thenReturn(false);

        ResponseEntity<Void> resposta = usuarioController.excluir("99");

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }
}

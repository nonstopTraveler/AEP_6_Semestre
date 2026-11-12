package com.aep.ler.service;

import com.aep.ler.model.Usuario;
import com.aep.ler.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId("1");
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@email.com");
        usuario.setCidade("Barbosa Ferraz");
        usuario.setEstado("PR");
    }

    @Test
    void testCadastrar() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.cadastrar(usuario);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.getNome());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testCadastrarEmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testListarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Maria Silva", resultado.get(0).getNome());
    }

    @Test
    void testBuscarPorId() {
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId("1");

        assertNotNull(resultado);
        assertEquals("maria@email.com", resultado.getEmail());
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(usuarioRepository.findById("99")).thenReturn(Optional.empty());

        assertNull(usuarioService.buscarPorId("99"));
    }

    @Test
    void testAtualizar() {
        Usuario dadosAtualizados = new Usuario();
        dadosAtualizados.setNome("Maria Souza");
        dadosAtualizados.setEmail("maria@email.com");
        dadosAtualizados.setCidade("Maringá");
        dadosAtualizados.setEstado("PR");

        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario resultado = usuarioService.atualizar("1", dadosAtualizados);

        assertEquals("Maria Souza", resultado.getNome());
        assertEquals("Maringá", resultado.getCidade());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testAtualizarUsuarioInexistente() {
        when(usuarioRepository.findById("99")).thenReturn(Optional.empty());

        assertNull(usuarioService.atualizar("99", usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testExcluir() {
        when(usuarioRepository.existsById("1")).thenReturn(true);

        assertTrue(usuarioService.excluir("1"));
        verify(usuarioRepository).deleteById("1");
    }

    @Test
    void testExcluirUsuarioInexistente() {
        when(usuarioRepository.existsById("99")).thenReturn(false);

        assertFalse(usuarioService.excluir("99"));
        verify(usuarioRepository, never()).deleteById("99");
    }
}
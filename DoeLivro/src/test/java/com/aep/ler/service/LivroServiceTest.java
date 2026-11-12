package com.aep.ler.service;

import com.aep.ler.repository.UsuarioRepository;
import com.aep.ler.repository.DoacaoRepository;
import com.aep.ler.model.Livro;
import com.aep.ler.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private DoacaoRepository doacaoRepository;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setId("1");
        livro.setTitulo("Dom Casmurro");
        livro.setAutor("Machado de Assis");
    }

    @Test
    void testListarTodos() {
        when(livroRepository.findAll()).thenReturn(Collections.singletonList(livro));

        List<Livro> resultado = livroService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dom Casmurro", resultado.get(0).getTitulo());
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorIdSucesso() {
        when(livroRepository.findById("1")).thenReturn(Optional.of(livro));

        Livro resultado = livroService.buscarPorId("1");

        assertNotNull(resultado);
        assertEquals("Dom Casmurro", resultado.getTitulo());
        verify(livroRepository, times(1)).findById("1");
    }

    @Test
    void testSalvar() {
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        Livro resultado = livroService.cadastrar(livro);

        assertNotNull(resultado);
        assertEquals("Dom Casmurro", resultado.getTitulo());
        verify(livroRepository, times(1)).save(livro);
    }

    @Test
    void testDeletar() {
        when(livroRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(livroRepository).deleteById(anyString());

        assertDoesNotThrow(() -> livroService.excluir("1"));

        verify(livroRepository, times(1)).existsById(anyString());
        verify(livroRepository, times(1)).deleteById(anyString());
    }

    @Test
    void testBuscarPorIdNaoEncontradoService() {
        when(livroRepository.findById("99")).thenReturn(Optional.empty());

        assertNull(livroService.buscarPorId("99"));
    }

    @Test
    void testExcluirLivroNaoExistenteService() {
        when(livroRepository.existsById("99")).thenReturn(false);

        assertDoesNotThrow(() -> livroService.excluir("99"));
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(livroRepository.findById("99")).thenReturn(Optional.empty());

        Livro resultado = livroService.buscarPorId("99");

        assertNull(resultado);
        verify(livroRepository, times(1)).findById("99");
    }

    @Test
    void testDeletarLivroInexistente() {
        when(livroRepository.existsById("99")).thenReturn(false);

        assertDoesNotThrow(() -> livroService.excluir("99"));

        verify(livroRepository, times(1)).existsById("99");
        verify(livroRepository, never()).deleteById("99");
    }

    @Test
    void testCadastrarLivroComDoadorExistente() {

        livro.setDoadorId("usuario01");

        when(usuarioRepository.existsById("usuario01"))
                .thenReturn(true);

        when(livroRepository.save(any(Livro.class)))
                .thenReturn(livro);

        Livro resultado = livroService.cadastrar(livro);

        assertNotNull(resultado);
        assertEquals("usuario01", resultado.getDoadorId());

        verify(usuarioRepository).existsById("usuario01");
        verify(livroRepository).save(livro);
    }

    @Test
    void testCadastrarLivroComDoadorInexistente() {

        livro.setDoadorId("usuario99");

        when(usuarioRepository.existsById("usuario99"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> livroService.cadastrar(livro)
        );

        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    void naoDeveExcluirLivroComDoacaoAssociada() {
        when(livroRepository.existsById("1")).thenReturn(true);
        when(doacaoRepository.existsByLivroId("1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> livroService.excluir("1"));
        verify(livroRepository, never()).deleteById("1");
    }

}
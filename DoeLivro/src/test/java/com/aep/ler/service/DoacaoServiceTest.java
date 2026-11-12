package com.aep.ler.service;

import com.aep.ler.model.Doacao;
import com.aep.ler.model.Livro;
import com.aep.ler.repository.DoacaoRepository;
import com.aep.ler.repository.LivroRepository;
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
class DoacaoServiceTest {

    @Mock private DoacaoRepository doacaoRepository;
    @Mock private LivroRepository livroRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private DoacaoService doacaoService;

    private Livro livro;
    private Doacao doacao;

    @BeforeEach
    void setUp() {
        livro = new Livro("Dom Casmurro", "Machado de Assis", "Romance", "Bom", true);
        livro.setId("livro-1");
        doacao = new Doacao();
        doacao.setLivroId("livro-1");
        doacao.setDoadorId("usuario-1");
    }

    @Test
    void criarDeveDefinirStatusHistoricoEAssociarLivro() {
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(usuarioRepository.existsById("usuario-1")).thenReturn(true);
        when(doacaoRepository.findByLivroId("livro-1")).thenReturn(List.of());
        when(doacaoRepository.save(any(Doacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Doacao resultado = doacaoService.criar(doacao);

        assertEquals("DISPONIVEL", resultado.getStatus());
        assertEquals(1, resultado.getHistorico().size());
        assertEquals("DISPONIVEL", resultado.getHistorico().get(0).getStatus());
        assertEquals("usuario-1", livro.getDoadorId());
        assertTrue(livro.isDisponivel());
        verify(livroRepository).save(livro);
        verify(doacaoRepository).save(doacao);
    }

    @Test
    void criarDeveRecusarLivroInexistente() {
        when(livroRepository.findById("livro-1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> doacaoService.criar(doacao));
        verify(doacaoRepository, never()).save(any());
    }

    @Test
    void criarDeveRecusarDoadorInexistente() {
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(usuarioRepository.existsById("usuario-1")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> doacaoService.criar(doacao));
        verify(doacaoRepository, never()).save(any());
    }

    @Test
    void criarDeveRecusarLivroAssociadoAOutroDoador() {
        livro.setDoadorId("usuario-2");
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(usuarioRepository.existsById("usuario-1")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> doacaoService.criar(doacao));
        verify(doacaoRepository, never()).save(any());
    }

    @Test
    void criarDeveRecusarDoacaoAtivaDuplicada() {
        Doacao existente = new Doacao();
        existente.setStatus("RESERVADO");
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(usuarioRepository.existsById("usuario-1")).thenReturn(true);
        when(doacaoRepository.findByLivroId("livro-1")).thenReturn(List.of(existente));
        assertThrows(IllegalStateException.class, () -> doacaoService.criar(doacao));
        verify(doacaoRepository, never()).save(any());
    }

    @Test
    void criarPermiteNovoRegistroQuandoAnteriorFoiCancelado() {
        Doacao cancelada = new Doacao();
        cancelada.setStatus("CANCELADO");
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(usuarioRepository.existsById("usuario-1")).thenReturn(true);
        when(doacaoRepository.findByLivroId("livro-1")).thenReturn(List.of(cancelada));
        when(doacaoRepository.save(any(Doacao.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals("DISPONIVEL", doacaoService.criar(doacao).getStatus());
    }

    @Test
    void buscarPorIdDeveRetornarDoacao() {
        Doacao esperada = new Doacao();
        when(doacaoRepository.findById("doacao-1")).thenReturn(Optional.of(esperada));
        assertSame(esperada, doacaoService.buscarPorId("doacao-1"));
    }

    @Test
    void buscarPorIdDeveFalharQuandoNaoExiste() {
        when(doacaoRepository.findById("inexistente")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> doacaoService.buscarPorId("inexistente"));
    }

    @Test
    void listarMetodosDevemConsultarRepositorio() {
        when(doacaoRepository.findAll()).thenReturn(List.of(doacao));
        when(doacaoRepository.findByDoadorId("usuario-1")).thenReturn(List.of(doacao));
        when(doacaoRepository.findByLivroId("livro-1")).thenReturn(List.of(doacao));
        when(doacaoRepository.findByStatus("DISPONIVEL")).thenReturn(List.of(doacao));
        assertEquals(1, doacaoService.listarTodos().size());
        assertEquals(1, doacaoService.listarPorDoador("usuario-1").size());
        assertEquals(1, doacaoService.listarPorLivro("livro-1").size());
        assertEquals(1, doacaoService.listarPorStatus("disponivel").size());
    }

    @Test
    void atualizarStatusRegistraHistoricoEAtualizaLivro() {
        doacao.setId("doacao-1");
        doacao.setStatus("DISPONIVEL");
        doacao.getHistorico().add(new Doacao.HistoricoStatus("DISPONIVEL", "Criada"));
        when(doacaoRepository.findById("doacao-1")).thenReturn(Optional.of(doacao));
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        when(doacaoRepository.save(any(Doacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Doacao resultado = doacaoService.atualizarStatus("doacao-1", "reservado", "Reserva confirmada");

        assertEquals("RESERVADO", resultado.getStatus());
        assertEquals(2, resultado.getHistorico().size());
        assertEquals("Reserva confirmada", resultado.getHistorico().get(1).getObservacao());
        assertFalse(livro.isDisponivel());
    }

    @Test
    void atualizarStatusDeveRecusarStatusDesconhecido() {
        doacao.setId("doacao-1");
        doacao.setStatus("DISPONIVEL");
        when(doacaoRepository.findById("doacao-1")).thenReturn(Optional.of(doacao));
        assertThrows(IllegalArgumentException.class,
                () -> doacaoService.atualizarStatus("doacao-1", "EM_ANALISE", "teste"));
    }

    @Test
    void naoDeveAlterarDoacaoQueJaFoiFinalizada() {
        doacao.setId("doacao-1");
        doacao.setStatus("DOADO");
        when(doacaoRepository.findById("doacao-1")).thenReturn(Optional.of(doacao));
        assertThrows(IllegalArgumentException.class,
                () -> doacaoService.atualizarStatus("doacao-1", "DISPONIVEL", "reabrir"));
        verify(doacaoRepository, never()).save(any());
    }

    @Test
    void excluirDeveRemoverDoacaoELimparAssociacaoDoLivro() {
        doacao.setId("doacao-1");
        livro.setDoadorId("usuario-1");
        when(doacaoRepository.findById("doacao-1")).thenReturn(Optional.of(doacao));
        when(livroRepository.findById("livro-1")).thenReturn(Optional.of(livro));
        doNothing().when(doacaoRepository).deleteById("doacao-1");

        assertDoesNotThrow(() -> doacaoService.excluir("doacao-1"));
        assertNull(livro.getDoadorId());
        assertTrue(livro.isDisponivel());
        verify(doacaoRepository).deleteById("doacao-1");
    }
}

package com.aep.ler.controller;

import com.aep.ler.model.Doacao;
import com.aep.ler.service.DoacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoacaoControllerTest {

    @Mock private DoacaoService doacaoService;
    @InjectMocks private DoacaoController controller;

    @Test
    void criarDeveDelegarAoServico() {
        Doacao entrada = new Doacao();
        entrada.setLivroId("l1");
        entrada.setDoadorId("u1");
        when(doacaoService.criar(entrada)).thenReturn(entrada);
        assertSame(entrada, controller.criar(entrada));
    }

    @Test
    void listarTodosDeveRetornarLista() {
        when(doacaoService.listarTodos()).thenReturn(List.of(new Doacao()));
        assertEquals(1, controller.listarTodos().size());
    }

    @Test
    void buscarPorIdDeveDelegarAoServico() {
        Doacao d = new Doacao();
        when(doacaoService.buscarPorId("d1")).thenReturn(d);
        assertSame(d, controller.buscarPorId("d1"));
    }

    @Test
    void filtrosDevemDelegarAoServico() {
        when(doacaoService.listarPorDoador("u1")).thenReturn(List.of());
        when(doacaoService.listarPorLivro("l1")).thenReturn(List.of());
        when(doacaoService.listarPorStatus("DISPONIVEL")).thenReturn(List.of());
        assertTrue(controller.listarPorDoador("u1").isEmpty());
        assertTrue(controller.listarPorLivro("l1").isEmpty());
        assertTrue(controller.listarPorStatus("DISPONIVEL").isEmpty());
    }

    @Test
    void atualizarStatusDevePassarCamposAoServico() {
        DoacaoController.AtualizacaoStatus req = new DoacaoController.AtualizacaoStatus();
        req.setStatus("RESERVADO");
        req.setObservacao("Reserva");
        Doacao resultado = new Doacao();
        when(doacaoService.atualizarStatus("d1", "RESERVADO", "Reserva")).thenReturn(resultado);
        assertSame(resultado, controller.atualizarStatus("d1", req));
    }

    @Test
    void excluirDeveDelegarAoServico() {
        assertDoesNotThrow(() -> controller.excluir("d1"));
        verify(doacaoService).excluir("d1");
    }
}

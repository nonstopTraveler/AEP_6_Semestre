package com.aep.ler.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoacaoTest {

    @Test
    void gettersESettersDevemManterDadosDaDoacao() {
        Doacao doacao = new Doacao();
        doacao.setId("d1");
        doacao.setLivroId("l1");
        doacao.setDoadorId("u1");
        doacao.setStatus("DISPONIVEL");
        doacao.setHistorico(List.of(new Doacao.HistoricoStatus("DISPONIVEL", "Cadastro")));

        assertEquals("d1", doacao.getId());
        assertEquals("l1", doacao.getLivroId());
        assertEquals("u1", doacao.getDoadorId());
        assertEquals("DISPONIVEL", doacao.getStatus());
        assertEquals(1, doacao.getHistorico().size());
        assertNotNull(doacao.getHistorico().get(0).getData());
    }

    @Test
    void historicoPermiteDefinirDataEObservacao() {
        Doacao.HistoricoStatus evento = new Doacao.HistoricoStatus();
        LocalDateTime agora = LocalDateTime.now();
        evento.setStatus("RESERVADO");
        evento.setData(agora);
        evento.setObservacao("Reserva realizada");
        assertEquals("RESERVADO", evento.getStatus());
        assertEquals(agora, evento.getData());
        assertEquals("Reserva realizada", evento.getObservacao());
    }

    @Test
    void historicoNuloDeveVirarListaVazia() {
        Doacao doacao = new Doacao();
        doacao.setHistorico(null);
        assertNotNull(doacao.getHistorico());
        assertTrue(doacao.getHistorico().isEmpty());
    }
}

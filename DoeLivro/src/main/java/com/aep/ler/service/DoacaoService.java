package com.aep.ler.service;

import com.aep.ler.model.Doacao;
import com.aep.ler.model.Livro;
import com.aep.ler.repository.DoacaoRepository;
import com.aep.ler.repository.LivroRepository;
import com.aep.ler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class DoacaoService {

    public static final String DISPONIVEL = "DISPONIVEL";
    public static final String RESERVADO = "RESERVADO";
    public static final String DOADO = "DOADO";
    public static final String CANCELADO = "CANCELADO";

    private static final Set<String> STATUS_VALIDOS =
            Set.of(DISPONIVEL, RESERVADO, DOADO, CANCELADO);

    private final DoacaoRepository doacaoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public DoacaoService(DoacaoRepository doacaoRepository,
                         LivroRepository livroRepository,
                         UsuarioRepository usuarioRepository) {
        this.doacaoRepository = doacaoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Doacao criar(Doacao doacao) {
        if (doacao == null) {
            throw new IllegalArgumentException("Os dados da doação são obrigatórios.");
        }

        String livroId = exigirTexto(doacao.getLivroId(), "O ID do livro é obrigatório.");
        String doadorId = exigirTexto(doacao.getDoadorId(), "O ID do doador é obrigatório.");

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        if (!usuarioRepository.existsById(doadorId)) {
            throw new IllegalArgumentException("Doador não encontrado.");
        }

        // Se o livro já estiver ligado a outro usuário, não cria uma relação conflitante.
        String doadorAtualDoLivro = livro.getDoadorId();
        if (doadorAtualDoLivro != null && !doadorAtualDoLivro.isBlank()
                && !doadorAtualDoLivro.equals(doadorId)) {
            throw new IllegalArgumentException(
                    "O livro já está associado a outro doador. Atualize o cadastro do livro primeiro.");
        }

        boolean existeDoacaoAtiva = doacaoRepository.findByLivroId(livroId).stream()
                .anyMatch(registro -> !CANCELADO.equalsIgnoreCase(registro.getStatus()));
        if (existeDoacaoAtiva) {
            throw new IllegalStateException("Este livro já possui uma doação ativa cadastrada.");
        }

        // O servidor controla ID, status e histórico inicial para evitar dados enviados pelo cliente.
        doacao.setId(null);
        doacao.setLivroId(livroId);
        doacao.setDoadorId(doadorId);
        doacao.setStatus(DISPONIVEL);
        doacao.setHistorico(new ArrayList<>());
        doacao.getHistorico().add(
                new Doacao.HistoricoStatus(DISPONIVEL, "Livro cadastrado para doação."));

        livro.setDoadorId(doadorId);
        livro.setDisponivel(true);
        livroRepository.save(livro);

        return doacaoRepository.save(doacao);
    }

    public List<Doacao> listarTodos() {
        return doacaoRepository.findAll();
    }

    public Doacao buscarPorId(String id) {
        return doacaoRepository.findById(exigirTexto(id, "O ID da doação é obrigatório."))
                .orElseThrow(() -> new IllegalArgumentException("Doação não encontrada."));
    }

    public List<Doacao> listarPorDoador(String doadorId) {
        return doacaoRepository.findByDoadorId(exigirTexto(doadorId, "O ID do doador é obrigatório."));
    }

    public List<Doacao> listarPorLivro(String livroId) {
        return doacaoRepository.findByLivroId(exigirTexto(livroId, "O ID do livro é obrigatório."));
    }

    public List<Doacao> listarPorStatus(String status) {
        return doacaoRepository.findByStatus(normalizarStatus(status));
    }

    public Doacao atualizarStatus(String id, String novoStatus, String observacao) {
        Doacao doacao = buscarPorId(id);
        String statusNormalizado = normalizarStatus(novoStatus);
        validarTransicao(doacao.getStatus(), statusNormalizado);

        if (statusNormalizado.equalsIgnoreCase(doacao.getStatus())) {
            return doacao;
        }

        doacao.setStatus(statusNormalizado);
        if (doacao.getHistorico() == null) {
            doacao.setHistorico(new ArrayList<>());
        }
        String textoHistorico = (observacao == null || observacao.isBlank())
                ? "Status atualizado para " + statusNormalizado + "."
                : observacao.trim();
        doacao.getHistorico().add(new Doacao.HistoricoStatus(statusNormalizado, textoHistorico));

        livroRepository.findById(doacao.getLivroId()).ifPresent(livro -> {
            livro.setDisponivel(DISPONIVEL.equals(statusNormalizado)
                    || CANCELADO.equals(statusNormalizado));
            if (CANCELADO.equals(statusNormalizado)) {
                livro.setDoadorId(null);
            }
            livroRepository.save(livro);
        });

        return doacaoRepository.save(doacao);
    }

    public void excluir(String id) {
        Doacao doacao = buscarPorId(id);
        List<Doacao> outrosRegistros = doacaoRepository.findByLivroId(doacao.getLivroId()).stream()
                .filter(registro -> !doacao.getId().equals(registro.getId()))
                .toList();
        boolean existeOutraDoacaoAtiva = outrosRegistros.stream()
                .anyMatch(registro -> !CANCELADO.equalsIgnoreCase(registro.getStatus()));

        if (!existeOutraDoacaoAtiva) {
            livroRepository.findById(doacao.getLivroId()).ifPresent(livro -> {
                if (doacao.getDoadorId() != null
                        && doacao.getDoadorId().equals(livro.getDoadorId())) {
                    livro.setDoadorId(null);
                }
                livro.setDisponivel(true);
                livroRepository.save(livro);
            });
        }
        doacaoRepository.deleteById(doacao.getId());
    }

    private String normalizarStatus(String status) {
        String valor = exigirTexto(status, "O status é obrigatório.").toUpperCase(Locale.ROOT);
        if (!STATUS_VALIDOS.contains(valor)) {
            throw new IllegalArgumentException(
                    "Status inválido. Use DISPONIVEL, RESERVADO, DOADO ou CANCELADO.");
        }
        return valor;
    }

    private void validarTransicao(String atual, String novo) {
        if (atual == null || atual.isBlank()) {
            return;
        }
        String statusAtual = atual.toUpperCase(Locale.ROOT);
        boolean permitida = switch (statusAtual) {
            case DISPONIVEL -> Set.of(RESERVADO, DOADO, CANCELADO).contains(novo);
            case RESERVADO -> Set.of(DISPONIVEL, DOADO, CANCELADO).contains(novo);
            case DOADO, CANCELADO -> false;
            default -> false;
        };
        if (!statusAtual.equals(novo) && !permitida) {
            throw new IllegalArgumentException(
                    "Não é permitido alterar o status de " + statusAtual + " para " + novo + ".");
        }
    }

    private String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }
}

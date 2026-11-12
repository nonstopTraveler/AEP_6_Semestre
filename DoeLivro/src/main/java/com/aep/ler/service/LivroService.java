package com.aep.ler.service;

import com.aep.ler.model.Livro;
import com.aep.ler.repository.DoacaoRepository;
import com.aep.ler.repository.LivroRepository;
import com.aep.ler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final DoacaoRepository doacaoRepository;

    public LivroService(LivroRepository livroRepository,
                        UsuarioRepository usuarioRepository,
                        DoacaoRepository doacaoRepository) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.doacaoRepository = doacaoRepository;
    }

    public Livro cadastrar(Livro livro) {
        validarDoador(livro.getDoadorId());
        return livroRepository.save(livro);
    }

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public Livro buscarPorId(String id) {
        return livroRepository.findById(id).orElse(null);
    }

    public Livro atualizar(String id, Livro livro) {
        Livro livroExistente = livroRepository.findById(id).orElse(null);
        if (livroExistente == null) return null;

        validarDoador(livro.getDoadorId());
        boolean possuiHistoricoDeDoacao = doacaoRepository.existsByLivroId(id);
        if (possuiHistoricoDeDoacao
                && !java.util.Objects.equals(livroExistente.getDoadorId(), livro.getDoadorId())) {
            throw new IllegalStateException(
                    "O doador de um livro com histórico de doação deve ser alterado pelo módulo de doações.");
        }
        livroExistente.setTitulo(livro.getTitulo());
        livroExistente.setAutor(livro.getAutor());
        livroExistente.setCategoria(livro.getCategoria());
        livroExistente.setEstadoConservacao(livro.getEstadoConservacao());
        if (!possuiHistoricoDeDoacao) {
            livroExistente.setDisponivel(livro.isDisponivel());
            livroExistente.setDoadorId(livro.getDoadorId());
        }
        return livroRepository.save(livroExistente);
    }

    public boolean excluir(String id) {
        if (!livroRepository.existsById(id)) return false;
        if (doacaoRepository.existsByLivroId(id)) {
            throw new IllegalStateException("Não é possível excluir um livro que possui histórico de doação. Exclua a doação antes.");
        }
        livroRepository.deleteById(id);
        return true;
    }

    private void validarDoador(String doadorId) {
        if (doadorId == null || doadorId.isBlank()) return;
        if (!usuarioRepository.existsById(doadorId)) {
            throw new IllegalArgumentException("O usuário informado como doador não existe.");
        }
    }
}

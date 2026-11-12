package com.aep.ler.service;

import com.aep.ler.model.Usuario;
import com.aep.ler.repository.DoacaoRepository;
import com.aep.ler.repository.LivroRepository;
import com.aep.ler.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final DoacaoRepository doacaoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                         LivroRepository livroRepository,
                         DoacaoRepository doacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.doacaoRepository = doacaoRepository;
    }

    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario atualizar(String id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);
        if (usuarioExistente == null) return null;

        boolean emailAlterado = !usuarioExistente.getEmail().equals(usuario.getEmail());
        if (emailAlterado && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail");
        }

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setTelefone(usuario.getTelefone());
        usuarioExistente.setCidade(usuario.getCidade());
        usuarioExistente.setEstado(usuario.getEstado());
        usuarioExistente.setEnderecos(usuario.getEnderecos());
        return usuarioRepository.save(usuarioExistente);
    }

    public boolean excluir(String id) {
        if (!usuarioRepository.existsById(id)) return false;
        if (livroRepository.existsByDoadorId(id) || doacaoRepository.existsByDoadorId(id)) {
            throw new IllegalStateException("Não é possível excluir um usuário associado a livros ou ao histórico de doações.");
        }
        usuarioRepository.deleteById(id);
        return true;
    }
}

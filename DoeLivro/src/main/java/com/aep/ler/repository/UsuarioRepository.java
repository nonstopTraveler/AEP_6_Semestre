package com.aep.ler.repository;

import com.aep.ler.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    boolean existsByEmail(String email);
}

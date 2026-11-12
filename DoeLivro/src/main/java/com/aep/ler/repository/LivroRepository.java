package com.aep.ler.repository;

import com.aep.ler.model.Livro;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LivroRepository extends MongoRepository<Livro, String> {
    boolean existsByDoadorId(String doadorId);
}

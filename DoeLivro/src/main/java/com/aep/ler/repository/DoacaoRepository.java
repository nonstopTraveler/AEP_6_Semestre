package com.aep.ler.repository;

import com.aep.ler.model.Doacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoacaoRepository extends MongoRepository<Doacao, String> {


    List<Doacao> findByDoadorId(String doadorId);

    List<Doacao> findByLivroId(String livroId);

    List<Doacao> findByStatus(String status);

    boolean existsByDoadorId(String doadorId);

    boolean existsByLivroId(String livroId);
}

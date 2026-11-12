package com.aep.ler.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "doacoes")
public class Doacao {

    @Id
    private String id;

    @NotBlank(message = "O ID do livro é obrigatório")
    private String livroId;

    @NotBlank(message = "O ID do doador é obrigatório")
    private String doadorId;

    // O status é definido pelo servidor. Não deve ser obrigatório no JSON de criação.
    private String status = "DISPONIVEL";

    // Lista de documentos embutidos no documento da doação (estrutura NoSQL complexa).
    private List<HistoricoStatus> historico = new ArrayList<>();

    public Doacao() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLivroId() { return livroId; }
    public void setLivroId(String livroId) { this.livroId = livroId; }

    public String getDoadorId() { return doadorId; }
    public void setDoadorId(String doadorId) { this.doadorId = doadorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<HistoricoStatus> getHistorico() { return historico; }

    public void setHistorico(List<HistoricoStatus> historico) {
        this.historico = historico == null ? new ArrayList<>() : new ArrayList<>(historico);
    }

    public static class HistoricoStatus {

        private String status;
        private LocalDateTime data;
        private String observacao;

        public HistoricoStatus() {
        }

        public HistoricoStatus(String status, String observacao) {
            this.status = status;
            this.data = LocalDateTime.now();
            this.observacao = observacao;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getData() { return data; }
        public void setData(LocalDateTime data) { this.data = data; }

        public String getObservacao() { return observacao; }
        public void setObservacao(String observacao) { this.observacao = observacao; }
    }
}

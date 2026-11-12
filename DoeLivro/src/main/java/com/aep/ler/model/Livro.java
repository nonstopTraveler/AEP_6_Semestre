package com.aep.ler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "livros")
public class Livro {

    @Id
    private String id;

    private String doadorId;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    private String autor;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "O estado de conservação é obrigatório")
    private String estadoConservacao;

    private boolean disponivel;

    public Livro() {
    }

    public Livro(String titulo, String autor, String categoria,
                 String estadoConservacao, boolean disponivel) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.estadoConservacao = estadoConservacao;
        this.disponivel = disponivel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getDoadorId() {
        return doadorId;
    }

    public void setDoadorId(String doadorId) {
        this.doadorId = doadorId;
    }
}
package com.aep.ler.controller;

import com.aep.ler.model.Doacao;
import com.aep.ler.service.DoacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doacao criar(@Valid @RequestBody Doacao doacao) {
        return doacaoService.criar(doacao);
    }

    @GetMapping
    public List<Doacao> listarTodos() {
        return doacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Doacao buscarPorId(@PathVariable String id) {
        return doacaoService.buscarPorId(id);
    }

    @GetMapping("/doador/{doadorId}")
    public List<Doacao> listarPorDoador(@PathVariable String doadorId) {
        return doacaoService.listarPorDoador(doadorId);
    }

    @GetMapping("/livro/{livroId}")
    public List<Doacao> listarPorLivro(@PathVariable String livroId) {
        return doacaoService.listarPorLivro(livroId);
    }

    @GetMapping("/status/{status}")
    public List<Doacao> listarPorStatus(@PathVariable String status) {
        return doacaoService.listarPorStatus(status);
    }

    @PatchMapping("/{id}/status")
    public Doacao atualizarStatus(@PathVariable String id,
                                  @Valid @RequestBody AtualizacaoStatus requisicao) {
        return doacaoService.atualizarStatus(
                id, requisicao.getStatus(), requisicao.getObservacao());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String id) {
        doacaoService.excluir(id);
    }

    public static class AtualizacaoStatus {
        @NotBlank(message = "O status é obrigatório")
        private String status;
        private String observacao;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getObservacao() { return observacao; }
        public void setObservacao(String observacao) { this.observacao = observacao; }
    }
}

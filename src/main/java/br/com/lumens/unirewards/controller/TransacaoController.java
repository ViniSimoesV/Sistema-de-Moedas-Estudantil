package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.TransacaoRequestDTO;
import br.com.lumens.unirewards.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<?> efetuarTransferencia(@RequestBody TransacaoRequestDTO dto) {
        try {
            transacaoService.processarTransferencia(dto);
            return ResponseEntity.ok(Map.of("mensagem", "Transferência processada com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("mensagem", "Erro crítico ao processar transação no servidor."));
        }
    }

    @GetMapping("/professor/{id}")
    public ResponseEntity<?> obterExtratoProfessor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transacaoService.listarExtratoProfessor(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensagem", "Erro ao buscar extrato do professor."));
        }
    }

    @GetMapping("/aluno/{id}")
    public ResponseEntity<?> obterExtratoAluno(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transacaoService.listarExtratoAluno(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensagem", "Erro ao buscar extrato do aluno."));
        }
    }
}
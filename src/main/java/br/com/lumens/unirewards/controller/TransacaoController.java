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
        transacaoService.processarTransferencia(dto);
        return ResponseEntity.ok(Map.of("mensagem", "Transferência processada com sucesso!"));
    }

    @GetMapping("/professor/{id}")
    public ResponseEntity<?> obterExtratoProfessor(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.listarExtratoProfessor(id));
    }

    @GetMapping("/aluno/{id}")
    public ResponseEntity<?> obterExtratoAluno(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.listarExtratoAluno(id));
    }
}
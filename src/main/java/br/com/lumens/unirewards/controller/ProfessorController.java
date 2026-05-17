package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.ProfessorDTO;
import br.com.lumens.unirewards.model.Professor;
import br.com.lumens.unirewards.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    // Rota para cadastrar um novo professor
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ProfessorDTO dto) {
        try {
            Professor professorSalvo = professorService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(professorSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ProfessorDTO dto) {
        try {
            Professor atualizado = professorService.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Rota para deletar um professor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        professorService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Rota para a Instituição buscar apenas os seus professores
    @GetMapping("/instituicao/{instituicaoId}")
    public ResponseEntity<List<Professor>> listarPorInstituicao(@PathVariable Long instituicaoId) {
        List<Professor> professores = professorService.listarPorInstituicao(instituicaoId);
        return ResponseEntity.ok(professores);
    }

    // Rota para o upload do CSV
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(
            @RequestParam("file") MultipartFile file, 
            @RequestParam("instituicaoId") Long instituicaoId) {
        try {
            List<Professor> cadastrados = professorService.processarCsv(file, instituicaoId);
            return ResponseEntity.ok(Map.of("mensagem", cadastrados.size() + " professores cadastrados com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Erro ao processar ficheiro: " + e.getMessage()));
        }
    }
}
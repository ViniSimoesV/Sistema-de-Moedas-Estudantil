package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.AlunoDTO;
import br.com.lumens.unirewards.model.Aluno;
import br.com.lumens.unirewards.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
//@CrossOrigin(origins = "*")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    // Endpoints CRUD para Aluno
    @PostMapping
    public ResponseEntity<Aluno> criar(@RequestBody AlunoDTO alunoDTO) {
        alunoService.salvar(alunoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Endpoint para listar todos os alunos
    @GetMapping
    public List<Aluno> listar() {
        return alunoService.listarTodos();
    }

    // Endpoint para buscar um aluno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscar(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para atualizar um aluno existente
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody AlunoDTO alunoDTO) {
        return ResponseEntity.ok(alunoService.atualizar(id, alunoDTO));
    }

    // Endpoint para deletar um aluno por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
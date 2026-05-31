package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.model.Inventario;
import br.com.lumens.unirewards.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventarios")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class InventarioController {

    // Aqui injetamos o repositório para poder acessar o banco de dados
    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Inventario>> listarInventarioDoAluno(@PathVariable Long alunoId) {
        // Agora o Java sabe quem é o inventarioRepository e o List!
        List<Inventario> inventarios = inventarioRepository.findByAlunoId(alunoId);
        return ResponseEntity.ok(inventarios);
    }
}
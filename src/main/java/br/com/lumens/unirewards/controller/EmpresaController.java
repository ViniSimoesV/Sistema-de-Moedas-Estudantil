package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.EmpresaDTO;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    // Endpoints RESTful para CRUD de Empresa
    @PostMapping
    public ResponseEntity<Empresa> criar(@RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.salvar(empresaDTO));
    }

    // Listar todas as empresas
    @GetMapping
    public List<Empresa> listar() {
        return empresaService.listarTodas();
    }

    // Buscar empresa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscar(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar empresa
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.atualizar(id, empresaDTO));
    }

    // Deletar empresa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
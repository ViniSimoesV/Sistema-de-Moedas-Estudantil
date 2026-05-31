package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.VantagemDTO;
import br.com.lumens.unirewards.model.Vantagem;
import br.com.lumens.unirewards.service.VantagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vantagens")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class VantagemController {

    @Autowired
    private VantagemService vantagemService;

    // Criar Vantagem
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody VantagemDTO dto) {
        try {
            Vantagem vantagemSalva = vantagemService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(vantagemSalva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/empresa/{empresaId}/top3")
    public ResponseEntity<List<Vantagem>> listarTop3PorEmpresa(@PathVariable Long empresaId) {
        // Chamando direto o repositório para simplificar, ou crie o método no Service
        return ResponseEntity.ok(vantagemService.listarTop3PorEmpresa(empresaId));
    }

    // Listar TODAS as Vantagens (Útil para a futura loja do Aluno)
    @GetMapping
    public ResponseEntity<List<Vantagem>> listarTodas() {
        return ResponseEntity.ok(vantagemService.listarTodas());
    }

    // Listar Vantagens apenas de uma Empresa específica (Painel da Empresa)
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Vantagem>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(vantagemService.listarPorEmpresa(empresaId));
    }

    // Filtrar vantagens por preço máximo (Ex: /api/vantagens/filtro/preco?maximo=500)
    @GetMapping("/filtro/preco")
    public ResponseEntity<List<Vantagem>> filtrarPorPreco(@RequestParam Integer maximo) {
        return ResponseEntity.ok(vantagemService.filtrarPorPrecoMaximo(maximo));
    }

    // Filtrar vantagens por ramo da empresa (Ex: /api/vantagens/filtro/ramo?nomeRamo=Alimentação)
    @GetMapping("/filtro/ramo")
    public ResponseEntity<List<Vantagem>> filtrarPorRamo(@RequestParam String nomeRamo) {
        return ResponseEntity.ok(vantagemService.filtrarPorRamo(nomeRamo));
    }

    // Buscar Vantagem por ID
    @GetMapping("/{id}")
    public ResponseEntity<Vantagem> buscarPorId(@PathVariable Long id) {
        return vantagemService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Editar Vantagem
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody VantagemDTO dto) {
        try {
            Vantagem vantagemAtualizada = vantagemService.atualizar(id, dto);
            return ResponseEntity.ok(vantagemAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }

    // Excluir Vantagem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vantagemService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
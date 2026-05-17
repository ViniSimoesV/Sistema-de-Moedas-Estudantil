package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.EmpresaDTO;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.repository.EmpresaRepository;
import br.com.lumens.unirewards.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;      // Correção do erro Map
import java.util.Optional; // Correção do erro Optional

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    // Injeção do repositório para fazer a busca do login
    @Autowired
    private EmpresaRepository repository;

    // Injeção do codificador de palavras-passe
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Endpoints RESTful para CRUD de Empresa
    @PostMapping
    public ResponseEntity<Empresa> criar(@RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.salvar(empresaDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        String cnpj = credenciais.get("cnpj");
        String senha = credenciais.get("senha");

        Optional<Empresa> empresaOpt = repository.findByCnpj(cnpj);

        if (empresaOpt.isPresent()) {
            Empresa emp = empresaOpt.get();
            
            // Valida a palavra-passe usando o BCrypt
            if (passwordEncoder.matches(senha, emp.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "id", emp.getId(),
                        "nome", emp.getNome(),
                        "cnpj", emp.getCnpj(),
                        "tipoUsuario", emp.getTipoUsuario()
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "CNPJ ou palavra-passe incorretos."));
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
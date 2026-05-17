package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.model.Instituicao;
import br.com.lumens.unirewards.repository.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/instituicoes")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class InstituicaoController {

    @Autowired
    private InstituicaoRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Instituicao instituicao) {
        if (repository.existsBySigla(instituicao.getSigla())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Esta sigla já está cadastrada!"));
        }

        instituicao.setSenha(passwordEncoder.encode(instituicao.getSenha()));
        instituicao.setTipoUsuario("INSTITUICAO"); 
        
        Instituicao salva = repository.save(instituicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        String sigla = credenciais.get("sigla");
        String senha = credenciais.get("senha");

        Optional<Instituicao> instituicaoOpt = repository.findBySigla(sigla);

        if (instituicaoOpt.isPresent()) {
            Instituicao inst = instituicaoOpt.get();
            
            // Valida a senha usando o BCrypt
            if (passwordEncoder.matches(senha, inst.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "id", inst.getId(),
                        "nome", inst.getNome(),
                        "sigla", inst.getSigla(),
                        "tipoUsuario", inst.getTipoUsuario()
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "Sigla ou senha incorretos."));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Instituicao dadosAtualizados) {
        return repository.findById(id).map(instituicao -> {
            instituicao.setNome(dadosAtualizados.getNome());
            instituicao.setSigla(dadosAtualizados.getSigla());
            
            // Só atualiza a foto se for enviada uma nova
            if (dadosAtualizados.getUrlFotoPerfil() != null && !dadosAtualizados.getUrlFotoPerfil().isEmpty()) {
                instituicao.setUrlFotoPerfil(dadosAtualizados.getUrlFotoPerfil());
            }

            Instituicao salva = repository.save(instituicao);
            return ResponseEntity.ok(salva);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
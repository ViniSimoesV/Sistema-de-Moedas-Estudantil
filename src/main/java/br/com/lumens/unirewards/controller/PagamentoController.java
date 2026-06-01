package br.com.lumens.unirewards.controller;

import br.com.lumens.unirewards.dto.ResgateDTO;
import br.com.lumens.unirewards.model.Inventario;
import br.com.lumens.unirewards.service.PagamentoService;
import br.com.lumens.unirewards.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "https://unirewards.vercel.app"})
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @PostMapping("/resgatar")
    public ResponseEntity<?> resgatarVantagem(@RequestBody ResgateDTO dto) {
        try {
            Inventario inventarioGerado = pagamentoService.resgatarVantagem(dto);
            return ResponseEntity.ok(Map.of(
                    "mensagem", "Pagamento aprovado! Cupom enviado para o seu e-mail.",
                    "cupom", inventarioGerado.getCodigoCupom(),
                    "status", inventarioGerado.getStatus()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao processar o resgate da vantagem."));
        }
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<?> listarPagamentosDoAluno(@PathVariable Long alunoId) {
        try {
            return ResponseEntity.ok(pagamentoRepository.findByAlunoId(alunoId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
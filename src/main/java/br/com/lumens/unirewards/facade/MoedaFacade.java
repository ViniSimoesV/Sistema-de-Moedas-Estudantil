package br.com.lumens.unirewards.facade;

import br.com.lumens.unirewards.service.AlunoService;
import br.com.lumens.unirewards.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoedaFacade {

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private EmpresaService empresaService;

    // Método para simplificar a troca de Lúmens por Vantagens
    public void processarResgateVantagem(Long alunoId, Long vantagemId) {
        // Lógica que verifica saldo, desconta da carteira e gera o cupom no inventário[cite: 14, 23]
    }
}
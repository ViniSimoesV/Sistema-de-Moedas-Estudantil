package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.VantagemDTO;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.model.Vantagem;
import br.com.lumens.unirewards.repository.EmpresaRepository;
import br.com.lumens.unirewards.repository.VantagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VantagemService {

    @Autowired
    private VantagemRepository vantagemRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Transactional
    public Vantagem salvar(VantagemDTO dto) {
        // Valida se a empresa informada no DTO realmente existe
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa parceira não encontrada no sistema!"));

        Vantagem vantagem = new Vantagem();
        vantagem.setNome(dto.getNome());
        vantagem.setDescricao(dto.getDescricao());
        vantagem.setCusto(dto.getCusto());
        vantagem.setUrlFoto(dto.getUrlFoto());
        vantagem.setEmpresa(empresa);

        return vantagemRepository.save(vantagem);
    }

    @Transactional
    public Vantagem atualizar(Long id, VantagemDTO dto) {
        Vantagem vantagem = vantagemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada!"));

        // Utilizando o método que você já declarou na entidade Vantagem.java
        vantagem.editarVantagem(dto.getNome(), dto.getDescricao(), dto.getCusto(), dto.getUrlFoto());

        return vantagemRepository.save(vantagem);
    }
    
    public List<Vantagem> listarTop3PorEmpresa(Long empresaId) {
        return vantagemRepository.findTop3ByEmpresaIdOrderByQuantidadeResgatesDesc(empresaId);
    }

    public List<Vantagem> listarTodas() {
        return vantagemRepository.findAll();
    }

    public List<Vantagem> listarPorEmpresa(Long empresaId) {
        return vantagemRepository.findByEmpresaId(empresaId);
    }

    public List<Vantagem> filtrarPorPrecoMaximo(Integer custoMaximo) {
        return vantagemRepository.findByCustoLessThanEqual(custoMaximo);
    }

    public List<Vantagem> filtrarPorRamo(String ramo) {
        return vantagemRepository.findByEmpresa_RamoAtuacaoContainingIgnoreCase(ramo);
    }

    public Optional<Vantagem> buscarPorId(Long id) {
        return vantagemRepository.findById(id);
    }

    @Transactional
    public void excluir(Long id) {
        if (vantagemRepository.existsById(id)) {
            vantagemRepository.deleteById(id);
        }
    }
}
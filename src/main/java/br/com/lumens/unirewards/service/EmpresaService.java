package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.dto.EmpresaDTO;
import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Empresa salvar(EmpresaDTO dto) {
        // Validação de CNPJ único
        if (empresaRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ.");
        }

        Empresa empresa = new Empresa();
        
        // Dados de Usuario 
        empresa.setNome(dto.getNome());
        empresa.setSenha(passwordEncoder.encode(dto.getSenha()));
        empresa.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        empresa.setTotalResgatados(dto.getTotalResgatados() != null ? dto.getTotalResgatados() : 0);
        empresa.setTipoUsuario("EMPRESA");

        // Dados específicos de Empresa
        empresa.setCnpj(dto.getCnpj());

        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa atualizar(Long id, EmpresaDTO dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        empresa.setNome(dto.getNome());
        empresa.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        empresa.setRamoAtuacao(dto.getRamoAtuacao());
        
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            empresa.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return empresaRepository.save(empresa);
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional
    public void excluir(Long id) {
        empresaRepository.deleteById(id);
    }
}
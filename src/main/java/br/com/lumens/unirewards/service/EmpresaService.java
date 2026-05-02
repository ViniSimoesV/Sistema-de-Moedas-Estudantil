package br.com.lumens.unirewards.service;

import br.com.lumens.unirewards.model.Empresa;
import br.com.lumens.unirewards.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    // CREATE / UPDATE: O save() serve para ambos
    @Transactional
    public Empresa salvar(Empresa empresa) {
        // adicionar validações para verificar se o e-mail já existe
        if (empresaRepository.findByCnpj(empresa.getCnpj()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ.");
        }
        return empresaRepository.save(empresa);
    }

    // READ: Busca todas as empresas
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    // READ: Busca por ID para o Perfil
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    // DELETE
    @Transactional
    public void excluir(Long id) {
        empresaRepository.deleteById(id);
    }

    // Método auxiliar para o login
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(cnpj);
    }
}
package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class AlunoDTO {
    // Dados de Usuario
    private String nome;
    private String senha; // Apenas no cadastro, não deve retornar no GET
    private String urlFotoPerfil;

    // Dados de UsuarioAcademico
    private String cpf;
    private String email;
    private Long instituicaoId;

    // Dados específicos de Aluno
    private String rg;
    private String curso;
    private int nivel;
    
    // Endereço
    private String rua;
    private Integer numero;
    private String bairro;
    private String cidade;
}
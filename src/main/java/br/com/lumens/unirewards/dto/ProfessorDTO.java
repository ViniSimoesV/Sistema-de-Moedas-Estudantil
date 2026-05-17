package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class ProfessorDTO {
    private String nome;
    private String cpf;
    private String email;
    private String departamento;
    private Long instituicaoId; 
}
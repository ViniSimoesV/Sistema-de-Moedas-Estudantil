package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String identificador; // Pode ser Email ou CNPJ
    private String senha;
}
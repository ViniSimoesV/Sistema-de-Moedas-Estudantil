package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class EmpresaDTO {
    private String nome;
    private String senha;
    private String cnpj;
    private String urlFotoPerfil;
}
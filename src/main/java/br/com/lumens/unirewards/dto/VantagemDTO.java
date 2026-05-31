package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class VantagemDTO {
    private String nome;
    private String descricao;
    private String urlFoto;
    private Integer custo;
    private Long empresaId;
}
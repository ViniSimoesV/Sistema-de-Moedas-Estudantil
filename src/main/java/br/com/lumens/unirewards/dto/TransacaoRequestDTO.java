package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class TransacaoRequestDTO {
    private Long remetenteId;
    private Long destinatarioId;
    private Integer valor;
    private String motivo;
    private String tipoRemetente; // "PROFESSOR" ou "ALUNO"
}
package br.com.lumens.unirewards.dto;

import lombok.Data;

@Data
public class EmailTransacaoDTO {
    private String emailDestino;
    private String nomeDestino;
    private String nomeOutraParte;
    private Integer valor;
    private String motivo;
    private String tipo; // "RECEBIDO" ou "ENVIADO"
}
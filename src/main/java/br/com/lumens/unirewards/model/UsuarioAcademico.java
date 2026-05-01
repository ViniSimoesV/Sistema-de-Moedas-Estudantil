package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "usuarios_academicos")
@PrimaryKeyJoinColumn(name = "usuario_id") // Faz o JOIN com a tabela Usuarios no Supabase
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class UsuarioAcademico extends Usuario {

    @Column(nullable = false, unique = true)
    private String cpf;

    @ManyToOne
    @JoinColumn(name = "instituicao_id") 
    private Instituicao instituicao;

}
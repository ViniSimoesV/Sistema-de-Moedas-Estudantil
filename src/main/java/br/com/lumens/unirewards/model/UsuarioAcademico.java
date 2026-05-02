package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "usuarios_academicos")
@PrimaryKeyJoinColumn(name = "usuario_id") 
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class UsuarioAcademico extends Usuario {

    @Column(nullable = false, unique = true)
    private String cpf;
    
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "instituicao_id") 
    private Instituicao instituicao;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carteira carteira;

}
package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "professores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Professor extends UsuarioAcademico {

    @Column(nullable = false)
    private String departamento;

    
    // Professor possui uma carteira para armazenar os Lúmens
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carteira carteira;
}
package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senha;

    @Column(name = "tipo_usuario")
    private String tipoUsuario; // Ex: 'ALUNO', 'PROFESSOR' ou 'EMPRESA'

    // Métodos para edição e exclusão de perfil
    public void editarPerfil() {
        // Lógica de edição no Service
    }

    public void excluirPerfil() {
        // Lógica de exclusão no Service
    }
}
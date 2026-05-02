package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "alunos")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Aluno extends UsuarioAcademico {

    @Column(nullable = false, unique = true)
    private String email;

    private String rg;
    private String curso;
    private int nivel = 1; // Nível inicial do aluno

    @Column(name = "end_rua")
    private String rua;

    @Column(name = "end_numero")
    private Integer numero;

    @Column(name = "end_complemento")
    private String complemento;

    @Column(name = "end_bairro")
    private String bairro;

    @Column(name = "end_cidade")
    private String cidade;

    // Um aluno tem uma carteira para acumular os Lúmens
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carteira carteira;

    // Dentro de Aluno.java
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<Inventario> cupons;

    // Métodos
    public Aluno cadastrarAluno(Aluno aluno) {
        // Lógica de cadastro no Service
        return aluno;
    }
}

package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "instituicoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instituicao_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "instituicao")
    @JsonIgnore
    private List<UsuarioAcademico> academicos;

    public void lancarListaProfessores(List<Professor> lista) {
        // Esta lógica será implementada no Service para validar e salvar em lote
    }
   
}
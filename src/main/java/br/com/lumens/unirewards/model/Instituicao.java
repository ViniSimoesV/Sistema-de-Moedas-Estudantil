package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "instituicoes")
@PrimaryKeyJoinColumn(name = "instituicao_id") 
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Instituicao extends Usuario {

    @Column(nullable = false, length = 20, unique = true)
    private String sigla;

    @OneToMany(mappedBy = "instituicao")
    @JsonIgnore
    private List<UsuarioAcademico> academicos;

    public void lancarListaProfessores(List<Professor> lista) {
        // Lógica para o upload do arquivo CSV/Excel
    }
}
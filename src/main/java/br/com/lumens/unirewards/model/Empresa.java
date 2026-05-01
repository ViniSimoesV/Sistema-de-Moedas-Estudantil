package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "empresas")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Empresa extends Usuario {

    @Column(nullable = false, unique = true)
    private String cnpj;

    // Relacionamento 1:N - Uma empresa oferece várias vantagens
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Vantagem> vantagens;

    // Métodos baseados no seu diagrama de classe
    public void gerenciarProfessor() {
        // Lógica para as empresas parceiras validarem professores
    }
}
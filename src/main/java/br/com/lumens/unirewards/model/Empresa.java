package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "total_resgatados", nullable = false)
    private Integer totalResgatados = 0;

    @Column(name = "ramo_atuacao")
    private String ramoAtuacao;

    // Uma empresa oferece várias vantagens
    @JsonIgnore
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Vantagem> vantagens;

    // Métodos
    public void gerenciarProfessor() {
        // Lógica para as empresas parceiras validarem professores
    }
}
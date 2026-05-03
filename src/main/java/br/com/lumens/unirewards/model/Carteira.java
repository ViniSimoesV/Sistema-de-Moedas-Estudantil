package br.com.lumens.unirewards.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carteiras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saldo_atual", nullable = false)
    private Integer saldoAtual = 0;

    // Um usuário tem uma carteira, e a carteira pertence a um usuário (professor/aluno)
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    @JsonBackReference
    @JsonIgnore
    private UsuarioAcademico usuario;

    // Métodos para receber Lúmens do semestre e verificar saldo
    public void receberPacoteSemestre(Integer valor) {
        this.saldoAtual += valor;
    }

    public boolean saldoSuficiente(Integer valor) {
        return this.saldoAtual >= valor;
    }
}
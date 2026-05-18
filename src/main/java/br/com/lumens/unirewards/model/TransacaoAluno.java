package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes_alunos")
@Data
public class TransacaoAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private Aluno remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Aluno destinatario;

    @Column(nullable = false)
    private Integer valor;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @PrePersist
    protected void onCreate() {
        this.dataEnvio = LocalDateTime.now();
    }
}
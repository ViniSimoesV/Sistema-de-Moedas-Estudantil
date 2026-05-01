package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventarios")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    @Column(name = "codigo_cupom", nullable = false, unique = true)
    private String codigoCupom;

    @Column(nullable = false)
    private String status; // DISPONIVEL, USADO, EXPIRADO

    @Column(name = "data_geracao")
    private LocalDateTime dataGeracao;

    @PrePersist
    protected void onCreate() {
        this.dataGeracao = LocalDateTime.now();
        // Gera um código único automaticamente ao criar o registro
        if (this.codigoCupom == null) {
            this.codigoCupom = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.status == null) {
            this.status = "DISPONIVEL";
        }
    }
}
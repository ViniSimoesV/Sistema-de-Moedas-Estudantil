package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    @Column(name = "data_compra")
    private LocalDateTime dataCompra;

    @Column(name = "valor_pago")
    private Integer valorPago;

    @PrePersist
    protected void onCreate() {
        this.dataCompra = LocalDateTime.now();
    }
}
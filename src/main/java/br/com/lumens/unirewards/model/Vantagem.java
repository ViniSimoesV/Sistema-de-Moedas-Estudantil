package br.com.lumens.unirewards.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "vantagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "url_foto", columnDefinition = "TEXT")
    private String urlFoto;

    @Column(nullable = false)
    private Integer custo; // Valor em Lúmens para resgate

    // Relacionamento ManyToOne: Muitas vantagens pertencem a uma única Empresa
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Métodos baseados no seu diagrama de classe
    public void cadastrarVantagem() {
        // Lógica de persistência será feita no VantagemService
    }

    public void consultarVantagem() {
        // Lógica de busca via Repository
    }

    public void editarVantagem(String novoNome, String novaDescricao, Integer novoCusto, String novaUrl) {
        this.nome = novoNome;
        this.descricao = novaDescricao;
        this.custo = novoCusto;
        this.urlFoto = novaUrl;
    }

    public void excluirVantagem() {
        // Lógica para desativar ou deletar a vantagem
    }
}
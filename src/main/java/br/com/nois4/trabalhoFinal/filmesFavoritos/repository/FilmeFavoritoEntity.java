package br.com.nois4.trabalhoFinal.filmesFavoritos.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "filmes_favoritos",
        uniqueConstraints = @UniqueConstraint(name = "uk_filme_titulo_normalizado", columnNames = "titulo_normalizado"))
public class FilmeFavoritoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String titulo;

    @Column(name = "titulo_normalizado", nullable = false, length = 120)
    public String tituloNormalizado;

    @Column(nullable = false, length = 100)
    public String diretor;

    @Column(nullable = false)
    public int ano;

    @Column(nullable = false, length = 60)
    public String genero;

    @Column(name = "criado_em", nullable = false)
    public Instant criadoEm;
}

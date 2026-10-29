package br.com.nois4.trabalhoFinal.filmesFavoritos.boundary;

import java.time.Instant;

public record FilmeFavoritoResponse(Long id, String titulo, String diretor, int ano, String genero, Instant criadoEm) {
}

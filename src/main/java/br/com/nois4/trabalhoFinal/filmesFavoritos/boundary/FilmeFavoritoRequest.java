package br.com.nois4.trabalhoFinal.filmesFavoritos.boundary;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilmeFavoritoRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotBlank @Size(max = 100) String diretor,
        @NotNull @Min(1888) @Max(2100) Integer ano,
        @NotBlank @Size(max = 60) String genero
) {
}

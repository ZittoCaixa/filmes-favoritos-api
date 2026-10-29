package br.com.nois4.trabalhoFinal.filmesFavoritos.domain;

public class FilmeDuplicadoException extends RuntimeException {
    public FilmeDuplicadoException(String titulo) {
        super("O filme '" + titulo + "' já está cadastrado como favorito.");
    }
}

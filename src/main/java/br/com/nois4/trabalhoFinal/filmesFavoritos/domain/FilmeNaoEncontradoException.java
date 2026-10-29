package br.com.nois4.trabalhoFinal.filmesFavoritos.domain;

public class FilmeNaoEncontradoException extends RuntimeException {
    public FilmeNaoEncontradoException(Long id) {
        super("Filme favorito com id " + id + " não foi encontrado.");
    }
}

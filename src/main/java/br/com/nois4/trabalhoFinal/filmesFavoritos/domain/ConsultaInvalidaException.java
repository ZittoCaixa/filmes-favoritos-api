package br.com.nois4.trabalhoFinal.filmesFavoritos.domain;

public class ConsultaInvalidaException extends RuntimeException {
    public ConsultaInvalidaException(String mensagem) {
        super(mensagem);
    }
}

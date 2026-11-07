package br.com.nois4.trabalhoFinal.filmesFavoritos.domain;

public class ErroNaConsultaException extends RuntimeException {
    public ErroNaConsultaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

package br.com.nois4.trabalhoFinal.plataform.error;

import java.time.Instant;

public record ErroResponse(String codigo, String mensagem, Instant timestamp) {
    public static ErroResponse de(String codigo, String mensagem) {
        return new ErroResponse(codigo, mensagem, Instant.now());
    }
}

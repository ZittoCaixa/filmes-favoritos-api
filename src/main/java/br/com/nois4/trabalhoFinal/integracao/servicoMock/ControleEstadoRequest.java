package br.com.nois4.trabalhoFinal.integracao.servicoMock;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ControleEstadoRequest {
    @NotNull
    private Boolean disponivel;
}
